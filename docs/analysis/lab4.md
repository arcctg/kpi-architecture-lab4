# Lab 4: Synchronous and Asynchronous Communication

## What changed compared to Lab 3?
In Lab 3, the system architecture was based on CQS. The logic executed strictly linearly: `Controller → Command/Query Handler → Domain → Repository`. The domain objects were persisted in the database, and the request execution ended there.

Lab 4 introduced the concept of side effects and integration events. Now, after a primary business action is completed, the system generates an event. Other independent components can react to this event. We extracted a separate auxiliary module that subscribes to these events and executes its tasks in the background without affecting the main execution flow.

## What side effects did you identify and why?
We extracted the activity log (audit) as our auxiliary component `com.flashcard.activitylog`.

**Why this specific component:**
- **Universality:** Activity logging is a classic cross-cutting concern that must react to all key mutations in the system (user registration, creating/updating/deleting decks and cards).
- **Independence:** Auditing is not part of the core flashcard domain. It has its own data model and persistence infrastructure like separate database table.
- **Non-blocking business logic:** If an error occurs while writing to the audit log, it must not interrupt the successful creation of a card by the user.
- **Preparation for Lab 5:** This activity log serves as a perfect foundation for building an isolated analytics context in the upcoming lab.

## Comparison of Synchronous vs. Asynchronous Approaches

| Criterion | Synchronous Approach (Direct Call) | Asynchronous Approach (Event Bus + @Async) |
| :--- | :--- | :--- |
| **API Response Time** | **Increased.** The client waits for the completion of both the main operation of saving a card and the auxiliary one of writing to the activity log. | **Optimal.** The client receives a response immediately after the card is saved and the event is published to the Event Bus. Auditing happens in a background thread. |
| **Failure Behavior** | **Not Isolated.** Requires explicit error handling (`try-catch` blocks around the `ActivityLogService` call). If `try-catch` is omitted, an audit exception will trigger a rollback of the main transaction. | **Isolated.** A failure in the background thread does not affect the main Handler execution flow. The client will still receive a successful response. |
| **Component Coupling** | **High.** The Command Handler explicitly knows about the existence of `ActivityLogService` and its methods. To add another side effect like an email notification, we would have to modify the Handler's code violating the Open/Closed Principle. | **Low.** The Handler only knows about the `EventPublisher` and simply publishes a fact. It doesn't know who processes it or how. New subscribers can be added without modifying the Handler code. |
| **Implementation & Testing Complexity** | **Low.** It is a simple interface method call. In tests, it is enough to mock the service and verify the interaction using `verify()`. | **Higher.** Requires setting up infrastructure (Event Bus, Thread Pools) and creating immutable Event DTOs. Testing is split: verifying event publication in the Handler test, and testing the Listener logic separately. |

## Which approach would you choose for a production system and why?
For a production system, I would choose the asynchronous approach for operations like auditing, email notifications, or analytics.

**Reasons:**
1. **Development Scalability:** When the system needs to send a welcome email upon registration, we simply add a new `@EventListener` without touching or bloating the `RegisterUseCase` code.
2. **Performance:** The user should not have to wait for slow secondary services to complete their work. The main transaction should be as fast as possible.
3. **Resilience:** A crash in the email delivery service or an analytics database timeout should not result in a 500 server error for a user who is just trying to create a new card.

The synchronous approach remains appropriate only when the side operation is part of a critical business invariant like checking and deducting funds from a balance before creating an order. However, for analytics and logging, this is not the case.

## What happens if the same event is delivered twice? Are your handlers idempotent?
In the current implementation, our `ActivityLogListener` is not idempotent. Every time it receives an event, it executes a `.save()` operation, unconditionally creating a new `ActivityLogEntry` object.

If an event is delivered through the Event Bus twice (for example, due to a message broker's retry mechanism), two identical duplicate records will appear in the `activity_log` table.

In the context of a simple audit or activity log, duplicating records is usually non-critical because it does not break the state of the core domain entities. However, to ensure strict idempotency in a production environment, we would need to implement a deduplication mechanism:
1. Add a unique event identifier (a UUID `eventId`) to the base event class.
2. Store the identifiers of processed events (e.g., use `eventId` as an Idempotency Key, or check its existence in an Inbox/Idempotency table).
3. If the Listener sees that an event with this `eventId` has already been processed, it simply returns a success without creating duplicates.
