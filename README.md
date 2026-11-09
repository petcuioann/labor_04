# library management system (thema 4)

spring boot 3.3.5 · java 17 · thymeleaf · maven · in-memory

## files by layer (29 java files & 15 thymeleaf templates)

- **root / config**: `pom.xml`, `.gitignore`, `src/main/resources/application.properties`, `LibraryApplication.java`, `DataInitializer.java`
- **model (domain)**: `Identifiable.java`, `Library.java`, `Member.java`, `Publication.java`, `BookDetails.java`, `MagazineDetails.java`, `ReadableItem.java`, `Author.java`, `BookAuthor.java`, `Loan.java`, `Reservation.java`
- **repository**: `CrudRepository.java`, `InMemoryRepository.java`, `RepositoryConfig.java`
- **service**: `LibraryService.java`, `CatalogService.java`, `CirculationService.java`
- **controller (REST & health)**: `HelloController.java`, `ApiController.java`
- **controller (Thymeleaf MVC)**: `HomeWebController.java`, `LibraryWebController.java`, `AuthorWebController.java`, `BookWebController.java`, `ItemWebController.java`, `MemberWebController.java`, `LoanWebController.java`, `ReservationWebController.java`
- **templates (Thymeleaf views)**: `templates/index.html`, `templates/library/{index,form}.html`, `templates/author/{index,form}.html`, `templates/book/{index,form}.html`, `templates/item/{index,form}.html`, `templates/member/{index,form}.html`, `templates/loan/{index,form}.html`, `templates/reservation/{index,form}.html`

## architectural decisions

- **repositories (DIP & DRY)**: `CrudRepository<T>` interface, `InMemoryRepository<T>` (`HashMap`), 8 beans in `RepositoryConfig`.
- **services (SRP)**: `LibraryService` (`Library`, `Member`), `CatalogService` (`Publication`, `BookDetails`, `MagazineDetails`, `ReadableItem`, `Author`, `BookAuthor`), `CirculationService` (`Loan`, `Reservation`). general `save` methods preserve existing child associations; unlinking is performed via dedicated methods (`unlinkAuthor`, `deleteItem`, `deleteMember`, `deleteLoan`, `deleteReservation`).
- **web MVC layer (Thymeleaf)**: dedicated `@Controller` per entity exposing `GET /<entity>`, `GET /<entity>/new`, `POST /<entity>`, and `POST /<entity>/{id}/delete`.
- **in-memory demo seeding**: `DataInitializer` (`CommandLineRunner`) pre-populates libraries, authors, books, magazines, physical copies, members, loans, and reservations on startup.
- **extensions**: `BookDetails` (`isbn`, `pageCount`), `MagazineDetails` (`issueNumber`), `Library` (`address`), `Member` (`email`), `Loan` (`dueDate`, `returnDate` as `LocalDate`).
