# library management system (thema 4)

- [deutsch (README_DE.md)](README_DE.md)
- [english (README_EN.md)](README_EN.md)

## files by layer (20 java files)

- **root / config**: `pom.xml`, `.gitignore`, `src/main/resources/application.properties`, `LibraryApplication.java`
- **model (domain)**: `Identifiable.java`, `Library.java`, `Member.java`, `Publication.java`, `BookDetails.java`, `MagazineDetails.java`, `ReadableItem.java`, `Author.java`, `BookAuthor.java`, `Loan.java`, `Reservation.java`
- **repository**: `CrudRepository.java`, `InMemoryRepository.java`, `RepositoryConfig.java`
- **service**: `LibraryService.java`, `CatalogService.java`, `CirculationService.java`
- **controller**: `HelloController.java`, `ApiController.java`

## architekturentscheidungen / architectural decisions

- **repositories (DIP & DRY)**: `CrudRepository<T>` interface, `InMemoryRepository<T>` (`HashMap`), 8 beans in `RepositoryConfig`.
- **services (SRP)**: `LibraryService` (`Library`, `Member`), `CatalogService` (`Publication`, `BookDetails`, `MagazineDetails`, `ReadableItem`, `Author`, `BookAuthor`), `CirculationService` (`Loan`, `Reservation`).
- **controllers**: `HelloController` (`GET /hello`), `ApiController` (`/api/**`).
- **extensions**: `BookDetails` (`isbn`, `pageCount`), `MagazineDetails` (`issueNumber`), `Library` (`address`), `Member` (`email`), `Loan` (`dueDate`, `returnDate` as `LocalDate`).
