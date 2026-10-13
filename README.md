# PokéCollector – Backend API

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-blue?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Maven](https://img.shields.io/badge/Maven-Build-red?style=flat-square&logo=apachemaven)](https://maven.apache.org/)

The backend core for **PokéCollector**, built with **Java and Spring Boot**. It provides a secure RESTful API for user authentication, Pokémon TCG card management, personal collections, wishlists, avatar uploads, and dynamic achievement tracking through the Hall of Fame system.

The card database is populated using the official Pokémon TCG data repository, which contains information about expansions and individual cards.

**Official Card Data Repository:** [PokemonTCG/pokemon-tcg-data](https://github.com/PokemonTCG/pokemon-tcg-data)

---

## Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Data Access:** Spring Data JPA / Hibernate
- **Security:** Spring Security with JSON Web Tokens (JWT) and OTP verification
- **Database:** MySQL
- **Build Tool:** Maven
- **Card Data Source:** [PokemonTCG/pokemon-tcg-data](https://github.com/PokemonTCG/pokemon-tcg-data)

---

## Project Architecture

The application follows a layered architecture, using the **DTO (Data Transfer Object)** pattern to decouple database entities from client-facing payloads.

```text
src/main/java/com/pokemon/tcg/
├── controller/       # REST API endpoints (Auth, Cards, Collection, Achievements, Admin)
├── dto/              # Request and response Data Transfer Objects
├── model/            # JPA entities (User, Card, Set, Achievement, etc.)
├── repository/       # Spring Data JPA interfaces for database operations
├── security/         # JWT filters and security configuration
└── service/          # Business logic and data import services
```

---

## Key Features

- **Secure Authentication & Authorization:** Role-based access control (`USER` / `ADMIN`), JWT authentication, and password recovery using email OTP verification.
- **Trainer Profiles & Avatars:** Multipart uploads for profile pictures and support for custom user settings.
- **Advanced Card Filtering & Pagination:** Database queries supporting card name searches, expansion sets, illustrators, dynamic sorting, and pagination.
- **Collection & Wishlist Management:** Endpoints for tracking owned cards, managing wishlists, and moving cards from the wishlist to the collection.
- **Hall of Fame & Achievements:** Automatic calculation of unlockable badges based on collection statistics and achievement configuration stored in JSON files.
- **Admin Dashboard Services:** Administrative tools for user management, data synchronization, and system administration.
- **Official Pokémon TCG Data Import:** Import expansion sets and card data from the official JSON dataset maintained by the PokémonTCG organization.

---

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- **JDK 17** or higher
- **MySQL 8.0** installed and running locally, or available through Docker
- **Maven** installed and configured in your system `PATH`
- **Git** to clone the repositories

### 1. Clone the Backend Repository

Clone the PokéCollector repository and navigate to the backend directory:

```bash
git clone https://github.com/MustacheOfDeath/PokemonTCG.git
cd PokemonTCG/backend
```

### 2. Download the Official Pokémon TCG Dataset

PokéCollector uses the JSON files from the official [PokemonTCG/pokemon-tcg-data](https://github.com/PokemonTCG/pokemon-tcg-data) repository to import expansion sets and cards into MySQL.

Clone the dataset repository into a separate directory on your computer:

```bash
git clone https://github.com/PokemonTCG/pokemon-tcg-data.git
```

After cloning, the dataset directory should contain the following structure:

```text
pokemon-tcg-data/
├── sets/
│   └── en.json
└── cards/
    └── en/
        ├── base1.json
        ├── base2.json
        └── ...
```

The `sets/en.json` file contains the English expansion set data. The `cards/en/` directory contains JSON files with card data, organized by expansion set.

**Important:** Clone the dataset repository separately from the backend repository. The import service reads the JSON files directly from the local filesystem; it does not automatically download the dataset.

### 3. Configure the Dataset Path

The `DataLoaderService` class contains a `manualImport()` method that reads the local JSON dataset and imports it into the application's database.

Inside `DataLoaderService.java`, locate the `BASE_PATH` variable:

```java
String BASE_PATH = "E:/Documenti/Programming/pokemon-tcg-data";
```

Replace the example path with the **absolute path to the directory where you cloned `pokemon-tcg-data`**.

For example, on Windows:

```java
String BASE_PATH = "C:/Users/YourUsername/Documents/Programming/pokemon-tcg-data";
```

On Linux:

```java
String BASE_PATH = "/home/yourusername/Programming/pokemon-tcg-data";
```

On macOS:

```java
String BASE_PATH = "/Users/yourusername/Programming/pokemon-tcg-data";
```

Use the actual path on your machine. Do not include the `sets` or `cards` directory at the end of `BASE_PATH`, because the import service appends these paths automatically.

The service expects these files and directories to exist:

- `${BASE_PATH}/sets/en.json`
- `${BASE_PATH}/cards/en/`

**Note:** The application must have permission to read the dataset files. If you move the repository or clone it into another directory, update `BASE_PATH` accordingly.

### 4. Configure Application Properties

Update `src/main/resources/application.properties` to match your environment.

The following example illustrates a local MySQL configuration:

```properties
spring.application.name=pokemon

# MySQL Database Connection
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate / JPA Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Hibernate Batch Processing
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Security
jwt.secret=${JWT_SECRET}

# File Upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

# Logging
logging.level.org.springframework.security=INFO
```

Make sure the MySQL JDBC driver dependency is included in `pom.xml`, for example through `mysql-connector-j`.

**Security note:** Never commit database credentials, mail passwords, or JWT secrets to version control. Use environment variables or a suitable secrets-management solution.

### Environment Variables

Configure the following environment variables before starting the backend:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | MySQL JDBC connection URL | `jdbc:mysql://localhost:3306/pokecollectordb` |
| `DB_USERNAME` | MySQL database username | `pokemon_user` |
| `DB_PASSWORD` | MySQL database password | Your database password |
| `MAIL_USERNAME` | Email account used to send OTP messages | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Email account app password | Your Gmail app password |
| `JWT_SECRET` | Secret used to sign JWTs | A securely generated secret |

Ensure that MySQL is running, the database exists, and the configured database user has the required permissions.

Hibernate can update the schema of existing tables, but it does not automatically create the database itself in every configuration.

### 5. Build and Run the Application

From the backend directory, start the application using Maven:

```bash
mvn spring-boot:run
```

Once the application has started, the API will typically be available at:

**http://localhost:8080**

---

## Importing Pokémon TCG Data

After configuring the dataset path and starting the backend, execute the `manualImport()` method in `DataLoaderService` through the application's existing import mechanism.

The method performs the following operations:

1. Reads expansion set data from `sets/en.json`.
2. Inserts new sets into MySQL or updates existing sets with non-null properties.
3. Scans the JSON files under `cards/en/`.
4. Processes each card file associated with a recognized expansion set.
5. Saves the imported card data through `CardProcessorService`.

If an error occurs, the method returns an error message containing the exception details.

**Important considerations:**

- Ensure the dataset has been cloned completely before importing.
- Run the import only after configuring the database connection.
- The import service uses the English dataset paths (`en.json` and `cards/en/`).
- The dataset repository can be updated independently. To import newer data, pull the latest changes and run the import again.
- Review the import logs if a set or card fails to process.

---

## Core API Endpoints

The following table summarizes the main API endpoints.

| Method | Endpoint | Description | Access |
|:---|:---|:---|:---|
| `POST` | `/api/auth/login` | Authenticates a user and returns a JWT | Public |
| `POST` | `/api/auth/register` | Registers a new trainer account | Public |
| `POST` | `/api/auth/password/request` | Requests an OTP for password recovery | Public |
| `GET` | `/api/cards` | Retrieves filtered, sorted, and paginated cards | Authenticated User |
| `GET` | `/api/collection` | Retrieves the authenticated user's card collection | Authenticated User |
| `GET` | `/api/wishlist` | Retrieves the authenticated user's wishlist | Authenticated User |
| `POST` | `/api/collection/toggle` | Adds or removes a card from the collection | Authenticated User |
| `POST` | `/api/wishlist/toggle` | Adds or removes a card from the wishlist | Authenticated User |
| `POST` | `/api/collection/move` | Moves a card from the wishlist to the collection | Authenticated User |
| `GET` | `/api/admin/users` | Lists and manages system users | Admin Only |

---

## Related Repositories

- **Backend:** [MustacheOfDeath/PokemonTCG](https://github.com/MustacheOfDeath/PokemonTCG)
- **Frontend:** [MustacheOfDeath/PokemonTCG-FRONTEND](https://github.com/MustacheOfDeath/PokemonTCG-FRONTEND)
- **Official Pokémon TCG Dataset:** [PokemonTCG/pokemon-tcg-data](https://github.com/PokemonTCG/pokemon-tcg-data)

---

*Built for Pokémon TCG collectors, providing the backend services needed to explore, organize, and manage their collections.*
