# Comparative Analysis: Lab 2 vs Lab 3

## 1. What changed in the project structure compared to Lab 2?

The transition from Lab 2 to Lab 3 replaced the monolithic `UseCase`-per-operation pattern with a strict Command-Query Separation (CQS) architecture. The `application` layer was restructured from flat use cases to a feature-oriented, intent-based design.

**In Lab 2:**

The Application layer contained a `usecase/` package with one class per operation: `CreateDeckUseCase`, `GetDeckUseCase`, `ListDecksUseCase`, etc. Each UseCase performed both write and read logic, returning domain-mapped DTOs directly. The read and write paths shared the same repositories (domain `DeckRepository`, `CardRepository`), and the same `CardResultMapper` handled all DTO conversions.

**In Lab 3:**

The Application layer is now split into two distinct pipelines per feature:
1. **`card/command/`** and **`deck/command/`** — Command DTOs (`AddCardCommand`, `CreateDeckCommand`) and their Handlers. Handlers perform writes through domain repositories and return only IDs.
2. **`card/query/`** and **`deck/query/`** — Query DTOs (`GetCardQuery`, `ListDecksQuery`), their Handlers, and dedicated `ReadRepository` interfaces. Handlers perform reads through read-optimized repositories and return DTOs directly, bypassing the domain model entirely.

The infrastructure layer gained two new adapters: `DeckReadRepositoryAdapter` and `CardReadRepositoryAdapter`, which implement the read repositories by querying JPA entities and mapping them to result DTOs without passing through domain models.

## 2. How did Command-Query Separation help?

**Clear intent.** Each operation is now explicitly categorized. A `CreateDeckCommand` unambiguously signals a state mutation, while a `GetDeckQuery` signals a side-effect-free read. In Lab 2, `CreateDeckUseCase` and `GetDeckUseCase` had the same structural pattern despite having fundamentally different semantics.

**Independent optimization.** Read and write paths can now be optimized independently. Query handlers use `@Transactional(readOnly = true)`, which allows the JPA provider to skip dirty checking and flush operations. Read repositories query JPA entities and map directly to DTOs, avoiding the overhead of constructing full domain aggregates with their invariants and value objects.

**Simplified testing.** The CQS pattern naturally produces two testing strategies:
- Command handlers are tested with **unit tests** using Mockito mocks: verify that domain factories are called, repositories receive the correct entities, and domain errors (AccessDenied, EntityNotFound) are thrown in edge cases.
- Query handlers are tested with **integration tests** through HTTP endpoints against a real database: verify correct field mapping, pagination, filtering, and response structure.

**Extensibility (Event Bus readiness).** Command handlers are natural points for emitting domain events. When Lab 4 introduces an EventBus for inter-module communication, events can be dispatched inside command handlers without modifying query handlers. In Lab 2, adding event publishing to a UseCase that also returned query results would have mixed concerns.

**Modular Monolith readiness.** The feature-based package structure (`card/command/`, `card/query/`, `deck/command/`, `deck/query/`) maps directly to future module boundaries in Lab 5. Each feature's commands and queries can be extracted into a separate module with minimal refactoring.

## 3. What are the disadvantages / complications?

**Increased number of classes.** The 10 UseCase classes from Lab 2 were replaced by 11 Command/Query DTOs, 11 Command/Query Handlers, 2 ReadRepository interfaces, and 2 ReadRepository adapters — roughly 26 classes. The CQS pattern trades code volume for explicit separation.

**Two-step response pattern.** In Lab 2, a create/update endpoint could call a single UseCase and return the result DTO. In Lab 3, the controller must first call the CommandHandler (which returns an ID), then call a QueryHandler to fetch the read model for the response. This results in two database round-trips for mutating endpoints.

**Read repository duplication.** The `DeckReadRepositoryAdapter` partially duplicates logic from the domain-level `DeckRepositoryAdapter` (e.g., looking up a user by email, finding a deck by ID). This is the conscious trade-off for keeping the read path independent of the domain model.

**Learning curve.** Developers must understand when to use a Command vs a Query, where to place read-only logic and why returning data from a command handler is discouraged. The mental model is more demanding than a straightforward UseCase.

## 4. How do Command/Query Handlers differ from a single Service?

In Lab 2, a `CreateDeckUseCase` was responsible for: validating the user, invoking the domain factory, saving the entity, and mapping the result to a DTO. It combined write intent, domain orchestration, and read model construction in a single class.

In Lab 3, these responsibilities are split:
- `CreateDeckCommandHandler` handles only the write: validate user, invoke factory, save, return ID.
- `GetDeckQueryHandler` handles only the read: call `ReadRepository`, return DTO.

This separation means that a command handler never needs to know about the read model structure, and a query handler never interacts with domain factories, value objects, or write repositories. Each handler has a single, well-defined responsibility.

## 5. How does CQS impact extensibility?

CQS dramatically improves extensibility by naturally enforcing the Open-Closed Principle. The architecture becomes open for extension but closed for modification.

**Adding a new command or query:** 
To add a new operation (e.g., `ArchiveDeckCommand`), you simply create a new command record and a new dedicated `ArchiveDeckCommandHandler`. You do not modify any existing handlers. Because the new handler is completely isolated, there is zero risk of causing regressions in existing features. Furthermore, the handler only injects the specific dependencies it needs.

**Contrast with Lab 2:**
In Lab 2, adding a new operation required creating a new `UseCase`. While better than a single massive `Service`, these UseCases often shared DTO mappers, repository logic, or data-fetching helper methods for building the response. If you needed to modify a shared mapper for a new UseCase, you risked breaking the response structure of an existing UseCase. By strictly separating Commands (which return no data) and Queries (which use dedicated read projections), CQS eliminates this coupling, making each feature truly additive.


## 6. Does the Query return structure differ from the domain model? Why does this matter?

Yes, the structures differ fundamentally. Queries return simple, flat DTOs (`DeckResult`, `CardResult`), whereas the domain model consists of rich Aggregates with encapsulated behavior and strongly typed Value Objects (like `DeckTitle` or `CardDefinition`).

This matters for three reasons:

1. **Separation of concerns:** The domain model is designed specifically to protect business invariants and handle complex state transitions (the write side). The query result structure, on the other hand, is tailored exclusively to the needs of the client UI (the read side).

2. **Avoiding over-fetching and optimizing performance:** To display a list of decks, the API doesn't need to load the full `Deck` aggregate with all its encapsulated rules and relationships from the database. The `ReadRepository` can execute an optimized SQL query that fetches exactly the columns needed and projects them directly into a DTO, bypassing the heavy ORM-to-Domain mapping overhead.

3. **Security and data hiding:** A domain aggregate might hold internal state, sensitive calculation fields, or versioning data required for business logic. By using a distinct read structure, we guarantee that we only expose the specific data intended for the client, preventing accidental data leaks.
