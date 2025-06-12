# Eksamensprojekt – Siren API

Dette repository indeholder et RESTful API til håndtering af sirener som en del af mit eksamensprojekt i Programmering, 3. semester på KEA.

## Om projektet

* **Uddannelse:** KEA – Københavns Erhvervsakademi
* **Semester:** 3. semester
* **Fag:** Programmering
* **Projekt:** Eksamensprojekt 3.semester
* **Forfatter:** Kalle Kvist

## Funktionalitet

* CRUD-endpoints til `SirenModel`:

  * **GET** `/api/sirens` – Hent alle sirener
  * **GET** `/api/sirens/{id}` – Hent en enkelt siren
  * **POST** `/api/sirens` – Opret en ny siren
  * **PUT** `/api/sirens/{id}` – Opdater en eksisterende siren
  * **DELETE** `/api/sirens/{id}` – Slet en siren

## Kørsel

1. Klon repository:

   ```bash
   git clone <repository-url>
   ```
2. Byg projektet med Maven:

   ```bash
   mvn clean install
   ```
3. Kør applikationen:

   ```bash
   mvn spring-boot:run
   ```
4. API’et er tilgængeligt på `http://localhost:8080/api/sirens`.

## H2 Console

* H2-webkonsol kan tilgås på `http://localhost:8080/h2-console`.
* JDBC URL: `jdbc:h2:mem:exam3db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`.
* Bruger: `sa`, ingen adgangskode.

---

*Eksamensprojekt i Programmering, 3. semester på KEA af Kalle Kvist.*
