# Comparative Analysis: Lab 1 vs Lab 2

## 1. What changed in the project structure compared to Lab 1?

The transition from Lab 1 to Lab 2 fundamentally reshaped the project from a flat structure to a strict 4-layered architecture based on DDD principles.

**In Lab 1:**

The project used a flat, feature-grouped package structure (`controller`, `service`, `repository`, `entity`). Business logic, data access via JPA, and web concerns via HTTP were tightly coupled. `Service` classes orchestrated everything, dealing directly with `JpaRepository` interfaces and ORM entities. Validation was partially handled by annotations and partially hardcoded into services. 

**In Lab 2:**

The system is now strictly divided into four distinct layers, enforcing the DIP:
1. **Domain Layer:** The pure core (`model`, `valueobject`, `factory`, `error`, `repository` interfaces). Contains 0 external dependencies (no Spring, no JPA).
2. **Application Layer:** Orchestrates business use cases. Depends only on the Domain. Defines ports for external services (`TokenProvider`, `PasswordEncoder`).
3. **Infrastructure Layer:** Implements Domain repositories using JPA (`adapter`, `jpa`, `entity`), handles security details via JWT implementations, and contains Spring configuration.
4. **Presentation Layer:** Handles HTTP requests, input validation, and routing.

## 2. What are the benefits of the layered architecture?

**Isolation of domain logic.** The Domain layer does not contain any Spring, JPA, or Jakarta imports. This is confirmed by an automated check: `grep "import org.springframework" domain/` returns 0 results. Business rules (Email validation, deck title uniqueness, term length limits) are protected by Value Objects and Domain Factories, independent of the infrastructure.

**Testability.** The 40 unit tests in the domain layer execute in ~2 seconds without a Spring Context or a database. In Lab 1, even the service unit tests implicitly depended on JPA-specific interfaces (`JpaRepository`). Now, factories and models are tested through pure interfaces using Mockito.

**Dependency Inversion.** Repositories are defined as interfaces in `domain.repository` and implemented by adapters in `infrastructure.persistence.adapter`. This allows for swapping the implementation without altering business logic. Similarly, the `PasswordEncoder` and `TokenProvider` ports isolate the Application layer from Spring Security and JJWT.

**CQS readiness (Lab 3).** The transition from massive `Service` classes to granular `UseCase` classes perfectly positions the project for the upcoming Command Query Separation pattern. Splitting `UseCase`s into `Commands` and `Queries` will be trivial.

**Single point of error handling.** The hierarchy `DomainError → EntityNotFoundError / DuplicateError / AccessDeniedError` allows the `GlobalExceptionHandler` in the Presentation layer to automatically map domain errors to HTTP statuses (400, 403, 404, 409) without leaking implementation details.

## 3. What are the disadvantages / complications?

**Increased code volume.** The number of files increased from ~30 to ~60. Each entity is now represented by three classes: a domain model (`Deck`), a JPA entity (`DeckEntity`), and a mapper (`DeckMapper`). This is the price paid for layer isolation.

**Mapping overhead.** Data passes through 3-4 transformations: `Request DTO → Value Object → Domain Model → JPA Entity` and back. Each mapper is a potential source of bugs when adding new fields.

**More complex navigation.** To trace the full path of a request (e.g., creating a card), one must traverse: `CardController → AddCardUseCase → CardFactory → CardRepository(interface) → CardRepositoryAdapter → JpaCardRepository → CardMapper`. In the flat structure, this was handled entirely within a single `CardService`.

**DTO duplication.** Separate DTOs exist for each layer: `CreateCardRequest` (Presentation), `CardResult` (Application), `CardResponse` (Presentation). Some of them have nearly identical fields, which gives the impression of redundancy.

**Scattered logic.** While responsibilities are clear, understanding the *complete* picture requires jumping between 4-5 different packages.

## 4. How much easier is it now to change the database or framework?

**Changing the database** requires modifications exclusively in `infrastructure.persistence`:
- New implementations for the interfaces in `domain.repository`
- New persistence entities
- New mappers

The entire `Domain`, `Application`, and `Presentation` layers will remain unchanged. In Lab 1, changing the database would have required refactoring services, controllers, and tests, since `JpaRepository` interfaces were used directly.

**Changing the framework** requires modifications in:
- `infrastructure/` — new configurations, DI annotations, security adapters
- `presentation/` — new controllers with the corresponding annotations

The Domain layer and Application Use Cases (excluding `@Service` and `@Transactional` annotations) remain portable. The `PasswordEncoder` and `TokenProvider` ports ensure that the Application layer has no direct dependencies on Spring Security.

## 5. Why was a Rich Domain Model chosen?

A Rich Domain Model was explicitly chosen over an Anemic Domain Model to ensure high cohesion and data integrity.

By choosing a Rich Domain Model:
1. **Behavior lives with Data:** The `Deck` and `Card` entities expose intention-revealing methods (`updateTitle()`, `updateDefinition()`, `isOwnedBy()`) rather than raw setters. 
2. **Encapsulation:** State transitions are strictly controlled. When `updateTitle()` is called on a `Deck`, it internally guarantees that the `updatedAt` timestamp is also refreshed. An Anemic model would rely on the developer remembering to update both fields in the service.
3. **Factories handle complex creation:** We avoided making the models too heavy. Checks that require database access (like verifying if a `Deck` title is unique) are delegated to a `Domain Factory`, striking a perfect balance between a pure domain and practical infrastructure needs.
