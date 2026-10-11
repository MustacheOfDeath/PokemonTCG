#  PokéCollector – Backend API

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-blue?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Maven](https://img.shields.io/badge/Maven-Build-red?style=flat-square&logo=apachemaven)](https://maven.apache.org/)

The backend core for **PokéCollector**, built with **Java and Spring Boot**. It provides a robust, secure, and scalable RESTful API for user authentication, card databases, collection management, wishlists, avatar uploads, and dynamic achievement tracking through the Hall of Fame system.

---

##  Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Data Access:** Spring Data JPA / Hibernate
- **Security:** Spring Security with JSON Web Tokens (JWT) and OTP verification
- **Database:** MySQL
- **Build Tool:** Maven

---

## Project Architecture

The application follows a clean, layered architecture, using the **DTO (Data Transfer Object)** pattern to decouple database entities from client-facing payloads.

```text
src/main/java/com/pokemon/tcg/
├── controller/       # REST API endpoints (Auth, Cards, Collection, Achievements, Admin)
├── dto/              # Request and response Data Transfer Objects
├── model/            # JPA entities (User, Card, Set, Achievement, etc.)
├── repository/       # Spring Data JPA interfaces for database operations
├── security/         # JWT filters, authentication entry points, and security configuration
└── service/          # Business logic layer
```

---

##  Key Features

- **Secure Authentication & Authorization:** Role-based access control (`USER` / `ADMIN`), JWT-based authentication, and secure password recovery using email OTP verification codes.
- **Trainer Profiles & Avatars:** Multipart file uploads for profile pictures and support for custom user settings.
- **Advanced Card Filtering & Pagination:** Efficient queries supporting card name searches, expansion sets, illustrators, dynamic sorting, and pagination.
- **Collection & Wishlist Management:** Endpoints for tracking owned cards, managing wishlists, and moving cards from the wishlist to the collection.
- **Hall of Fame & Achievements:** Automatic calculation of unlockable badges based on user collection statistics and achievement configuration stored in JSON files.
- **Admin Dashboard Services:** Administrative tools for user management, data synchronization, and system administration.

---

##  Getting Started

### Prerequisites

Make sure the following tools are installed:

- **JDK 17** or higher
- **MySQL 8.0** installed and running locally, or available through Docker.
- **Maven** installed and configured in your system `PATH`

### 1. Clone the Repository

```bash
git clone https://github.com/MustacheOfDeath/PokemonTCG.git
cd PokemonTCG/backend
```

### 2. Configure Application Properties

Update `src/main/resources/application.properties` to match your environment.

The following example uses a MySQL database for local development and testing.

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
logging.level.org.springframework.security=DEBUG
```

> **Note:** Make sure the MySQL JDBC driver dependency is included in your project (e.g., `mysql-connector-j` in your `pom.xml`). For production deployments, configure your database credentials securely using environment variables or an appropriate secrets-management solution.

> **Logging:** `DEBUG` logging is intended for development and troubleshooting only. For production environments, set `logging.level.org.springframework.security=INFO` to reduce diagnostic output and avoid exposing potentially sensitive security-related information in logs.

#### Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | MySQL JDBC connection URL | `jdbc:mysql://localhost:3306/pokecollectordb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | MySQL database username | `pokemon_user` |
| `DB_PASSWORD` | MySQL database password | Your database password |
| `MAIL_USERNAME` | Gmail address used to send emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Gmail app password | Your Gmail app password |
| `JWT_SECRET` | Secret key used to sign JWTs | A securely generated, sufficiently long secret |

Make sure MySQL is running and the database specified in `DB_URL` exists before starting the application. The configured database user must have the necessary permissions. Hibernate can update the schema of existing tables, but it does not automatically create the database itself.

**Security note:** Never commit real credentials or JWT secrets to version control.

### 3. Build and Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Once the application has started, the API will be available at:

**http://localhost:8080**

---

##  Core API Endpoints

The following table provides an overview of the main API endpoints.

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
