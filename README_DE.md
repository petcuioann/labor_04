# library management system (thema 4)

spring boot 3 · java 17 · maven · in-memory

## dateien nach schichten (20 java-dateien)

- **root / config**: `pom.xml`, `.gitignore`, `src/main/resources/application.properties`, `LibraryApplication.java`
- **model (domain)**: `Identifiable.java`, `Library.java`, `Member.java`, `Publication.java`, `BookDetails.java`, `MagazineDetails.java`, `ReadableItem.java`, `Author.java`, `BookAuthor.java`, `Loan.java`, `Reservation.java`
- **repository**: `CrudRepository.java`, `InMemoryRepository.java`, `RepositoryConfig.java`
- **service**: `LibraryService.java`, `CatalogService.java`, `CirculationService.java`
- **controller**: `HelloController.java`, `ApiController.java`

## architekturentscheidungen

- **repositories (DIP & DRY)**: `CrudRepository<T>` als interface, `InMemoryRepository<T>` (`ConcurrentHashMap`) als generische implementierung, 8 separate beans in `RepositoryConfig` (ein repository pro entität ohne code-duplikate).
- **services (SRP)**: 3 fachlich abgegrenzte services statt 9 einzelner:
  - `LibraryService`: verwaltet `Library` und `Member`
  - `CatalogService`: verwaltet `Publication` (`BookDetails`, `MagazineDetails`), `ReadableItem`, `Author`, `BookAuthor`
  - `CirculationService`: verwaltet `Loan`, `Reservation` und alle statusübergänge von `ReadableItem`
- **controller**: `HelloController` (`GET /hello`) für den Basistest; `ApiController` (`/api/**`) bündelt alle REST-endpunkte und die fehlerbehandlung (`400`, `404`, `409`).
- **modell & typen**: `Publication` ist abstrakt (`BookDetails`, `MagazineDetails` erben davon). datumsfelder nutzen `LocalDate`. statuswerte sind als `enum` (`AVAILABLE`, `BORROWED`, `RESERVED` bzw. `ACTIVE`, `CANCELLED`, `COMPLETED`) definiert.

## modellerweiterungen

- `BookDetails`: `isbn`, `pageCount`
- `MagazineDetails`: `issueNumber`
- `Library`: `address`
- `Member`: `email`
- `Loan`: `dueDate`, `returnDate`
