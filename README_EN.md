# library management system (topic 4)

spring boot 3 · java 17 · maven · in-memory

## files by layer (20 java files)

- **root / config**: `pom.xml`, `.gitignore`, `src/main/resources/application.properties`, `LibraryApplication.java`
- **model (domain)**: `Identifiable.java`, `Library.java`, `Member.java`, `Publication.java`, `BookDetails.java`, `MagazineDetails.java`, `ReadableItem.java`, `Author.java`, `BookAuthor.java`, `Loan.java`, `Reservation.java`
- **repository**: `CrudRepository.java`, `InMemoryRepository.java`, `RepositoryConfig.java`
- **service**: `LibraryService.java`, `CatalogService.java`, `CirculationService.java`
- **controller**: `HelloController.java`, `ApiController.java`

## architectural decisions

- **repositories (DIP & DRY)**: `CrudRepository<T>` interface, `InMemoryRepository<T>` (`HashMap`) generic implementation, 8 separate beans in `RepositoryConfig` (one repository per entity without boilerplate subclasses).
- **services (SRP)**: 3 domain-scoped services:
  - `LibraryService`: manages `Library` and `Member`
  - `CatalogService`: manages `Publication` (`BookDetails`, `MagazineDetails`), `ReadableItem`, `Author`, `BookAuthor`
  - `CirculationService`: manages `Loan`, `Reservation`, and all `ReadableItem` status transitions
- **controllers**: `HelloController` (`GET /hello`) for health check; `ApiController` (`/api/**`) consolidates all REST endpoints.
- **model & types**: `Publication` is abstract (extended by `BookDetails`, `MagazineDetails`). dates use `LocalDate`. statuses use `enum` (`AVAILABLE`, `BORROWED`, `RESERVED` and `ACTIVE`, `CANCELLED`, `COMPLETED`).

## model extensions

- `BookDetails`: `isbn`, `pageCount`
- `MagazineDetails`: `issueNumber`
- `Library`: `address`
- `Member`: `email`
- `Loan`: `dueDate`, `returnDate`
