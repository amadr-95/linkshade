# LinkShade

<div style="text-align: center;">

A modern URL shortening service built with Spring Boot and PostgreSQL, featuring OAuth authentication, rate limiting, and comprehensive URL management capabilities.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)

</div>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Database Management](#-database-management)
- [Performance Optimization](#-performance-optimization)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)

---

## 🎯 Overview

LinkShade is a production-ready URL shortening service designed with scalability, security, and user experience in mind. It provides both public and authenticated access, with robust rate limiting, OAuth integration, and comprehensive URL management features.

### Purpose

- **Simplify URL Sharing**: Convert long URLs into short, manageable links
- **Track Engagement**: Monitor clicks
- **Secure Access Control**: Support for both public and private URLs with user authentication
- **Multi-tenant Support**: User-specific URL management with admin capabilities
- **Enterprise-Ready**: Built with production best practices including rate limiting, validation, and error handling

---

## ✨ Key Features

### Core Functionality
- ✅ **URL Shortening**: Generate short, unique identifiers for long URLs
- ✅ **Custom Short Codes**: Create personalized short URL identifiers
- ✅ **Click Tracking**: Monitor usage statistics for each shortened URL
- ✅ **URL Expiration**: Automatic expiration with configurable time periods
- ✅ **Bulk Operations**: Delete or reactivate multiple URLs at once
- ✅ **User Profiles**: Manage URLs fields
- ✅ **Admin Dashboard**: Administrative interface for user and URL management
- 
### Authentication & Security
- ✅ **OAuth 2.0 Integration**: Login with Google and GitHub
- ✅ **Rate Limiting**: Separate limits for anonymous and authenticated users
- ✅ **Private URLs**: User-specific URL access control
- ✅ **Input Validation**: Comprehensive validation for URLs and user input

### UI/UX
- ✅ **Responsive Design**: Bootstrap 5-based modern interface
- ✅ **Interactive Carousel**: Product feature showcase
- ✅ **Pagination & Sorting**: Efficient data browsing with customizable views
- ✅ **Error Pages**: Custom error handling with user-friendly messages

---

## 🛠 Technology Stack

### Backend
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.5.7**: Enterprise-grade application framework
- **Spring Security**: OAuth 2.0 and authentication/authorization
- **Spring Data JPA**: Database abstraction and ORM
- **Hibernate**: JPA implementation for entity management
- **Maven**: Dependency management and build automation

### Database
- **PostgreSQL**: Primary relational database
- **Flyway**: Database version control and migration management

### Frontend
- **Thymeleaf**: Server-side template engine
- **Bootstrap 5.3.5**: Responsive UI framework
- **Bootstrap Icons 1.11.3**: Icon library
- **JavaScript**: Client-side interactivity

### DevOps & Tools
- **Docker**: Containerization for consistent environments
- **Docker Compose**: Multi-container orchestration
- **Lombok**: Boilerplate code reduction
- **JUnit 5**: Testing framework

### Development Tools
- **Maven Wrapper**: Ensures consistent Maven version
- **Eclipse Temurin JDK**: OpenJDK distribution for builds

---

## 🏗 Architecture

### Domain-Driven Design

The application follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers, Templates, Interceptors) │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Service Layer                  │
│  (Business Logic, Validation, Mapping)  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Repository Layer                │
│    (Data Access, JPA Repositories)      │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Database Layer                 │
│           (PostgreSQL)                  │
└─────────────────────────────────────────┘
```

### Core Entities

The system is built around two primary domain entities:

- **`User`**: Manages authenticated user information, OAuth provider data, and relationships to shortened URLs
- **`ShortUrl`**: Stores shortened URLs with metadata including clicks, expiration dates, and privacy settings

---

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

| Tool               | Version | Purpose                                                         |
|--------------------|---------|-----------------------------------------------------------------|
| **Java JDK**       | 21+     | Required for running the application                            |
| **Maven**          | 3.8+    | Build and dependency management (or use included Maven Wrapper) |
| **Docker**         | Latest  | For containerized PostgreSQL and application deployment         |
| **Docker Compose** | Latest  | For multi-container orchestration                               |
| **Git**            | Latest  | Version control                                                 |

---

## 🚀 Getting Started

Get up and running in 5 minutes (using `docker compose`):

```bash
# Clone the repository
git clone https://github.com/yourusername/linkshade.git
cd linkshade

# Set up environment variables (optional - see table below)
cp .env.example .env.local
```

### 📊 Environment Variables Reference

| Variable                     | Description                  | Required | Default | Example                                         |
|------------------------------|------------------------------|----------|---------|-------------------------------------------------|
| `GITHUB_CLIENT_ID`           | GitHub OAuth client ID       | No*      | -       | `abc123def456`                                  |
| `GITHUB_CLIENT_SECRET`       | GitHub OAuth client secret   | No*      | -       | `xyz789uvw456`                                  |
| `GOOGLE_CLIENT_ID`           | Google OAuth client ID       | No*      | -       | `123456.apps.googleusercontent.com`             |
| `GOOGLE_CLIENT_SECRET`       | Google OAuth client secret   | No*      | -       | `GOCSPX-abc123`                                 |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC URL          | Yes      | -       | `jdbc:postgresql://localhost:5432/linkshade-db` |
| `SPRING_DATASOURCE_USERNAME` | Database username            | Yes      | -       | `postgres`                                      |
| `SPRING_DATASOURCE_PASSWORD` | Database password            | Yes      | -       | `postgres`                                      |

\* *Required for OAuth login functionality. Application will work without OAuth but users won't be able to log in.*

> [!NOTE]
> If you don't mind having this feature, skip this part and continue with running the app from [here](#option-1)

To enable OAuth authentication, you'll need:
- **Google OAuth 2.0 Credentials**: [Google Cloud Console](https://console.cloud.google.com/)
- **GitHub OAuth App**: [GitHub Developer Settings](https://github.com/settings/developers)

#### Google OAuth Configuration

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Create OAuth 2.0 credentials:
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
5. Copy the Client ID and Client Secret to your `.env.local` file

#### GitHub OAuth Configuration

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in the application details:
   - Application name: LinkShade
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Copy the Client ID and Client Secret to your `.env.local` file

---

### Option 1

Run the database in Docker and the application from your IDE or Maven:

#### Step 1: Start PostgreSQL

```bash
docker compose up -d linkshade-db
```

#### Step 2: Run the Application
> [!NOTE]
> You need to add the `env.local` path to the run configuration, otherwise
> env variables won't be loaded and app won't be able to connect to the database

Run `LinkshadeApplication.java` or 
```bash
./mvnw spring-boot:run
```

#### Step 3: Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

#### Step 4: Stop the Database (when finished)

```bash
docker compose down
```

To remove all data:
```bash
docker compose down -v
```

---

### Option 2 (Recommended)

Run both the database and application as Docker containers:

#### Step 1: Build and Start All Services

```bash
docker compose up -d --build
```

> [!TIP]  
> Omit `--build` flag if no code changes were made since the last build.

#### Step 2: Verify Services are Running

```bash
docker compose ps
```

Expected output:
```
NAME              IMAGE              STATUS         PORTS
linkshade-app     linkshade-app      Up             0.0.0.0:8080->8080/tcp
linkshade-db      postgres           Up (healthy)   0.0.0.0:5432->5432/tcp
```

#### Step 3: View Logs

**All services:**
```bash
docker compose logs -f
```

**Specific service:**
```bash
docker compose logs -f [NAME]
```

#### Step 4: Access the Application

```
http://localhost:8080
```

#### Step 5: Stop All Services

```bash
docker compose down
```

**To remove all data (including database volumes):**
```bash
docker compose down -v
```

---

## 📁 Project Structure

```
linkshade/
├── src/
│   ├── main/
│   │   ├── java/de/linkshade/
│   │   │   ├── config/              # Application configuration
│   │   │   ├── domain/              # Domain entities and DTOs
│   │   │   │   ├── entities/        # JPA entities
│   │   │   │   └── dto/             # Data Transfer Objects
│   │   │   ├── exceptions/          # Custom exceptions and handlers
│   │   │   ├── repositories/        # Spring Data JPA repositories
│   │   │   ├── security/            # Security configuration and OAuth
│   │   │   ├── services/            # Business logic layer
│   │   │   ├── utils/               # Utility classes
│   │   │   ├── validation/          # Custom validators
│   │   │   └── web/                 # Controllers and interceptors
│   │   └── resources/
│   │       ├── db/migration/        # Flyway migration scripts
│   │       ├── static/              # Static assets (CSS, JS, images)
│   │       ├── templates/           # Thymeleaf templates
│   │       ├── application.yml      # Main configuration
│   │       └── application-prod.yml # Production configuration
│   └── test/                        # Test files
├── docker-compose.yml               # Docker Compose configuration
├── Dockerfile                       # Multi-stage Docker build
├── pom.xml                          # Maven configuration
├── .env.example                     # Environment variables template
└── README.md                        # This file
```

---

## 🗄 Database Management

### Flyway Migrations

LinkShade uses Flyway for database version control and migration management. This ensures:

- **Version Control**: Track all database schema changes
- **Consistency**: Apply migrations uniformly across all environments
- **Rollback Safety**: Maintain migration history for audit trails
- **Team Collaboration**: Avoid schema conflicts

#### Migration Structure

Flyway migrations are located in `src/main/resources/db/migration/`:

```
db/migration/
├── V1__create_schema.sql      # Initial schema creation
├── V2__insert_data.sql        # Seed data
└── V{n}__{description}.sql   # Subsequent migrations
```

#### Naming Convention

- **Format**: `V{version}__{description}.sql`
- **Example**: `V3__add_user_roles.sql`
- **Rules**:
  - Version must be numeric and unique
  - Double underscore separates version from description
  - Description uses underscores for spaces

## ⚡ Performance Optimization

### Preventing the N+1 Select Problem

The application implements best practices to avoid the N+1 query problem:

```yaml
spring:
  jpa:
    open-in-view: false
```

**Benefits:**
- ✅ Closes `EntityManager` at transaction boundary
- ✅ Prevents lazy loading outside transaction context
- ✅ Forces explicit data fetching strategies
- ✅ Releases database connections promptly
- ✅ Reduces database load

**Implementation Strategy:**

1. **Lazy Loading by Default**: All relationships are marked with `FetchType.LAZY`
2. **Explicit Fetching**: Use `@EntityGraph` or JPQL JOIN FETCH for related data
3. **Single Query Execution**: Ensures only one query per operation instead of N+1

**Example:**

```java
@EntityGraph(attributePaths = {"user"})
@Query("SELECT u FROM ShortUrl u WHERE u.id = :id")
Optional<ShortUrl> findByIdWithUser(@Param("id") Long id);
```

---

## 📸 Screenshots

### Home Page
_Coming soon - URL shortening interface_

### User Dashboard
_Coming soon - Personal URL management_

### Admin Dashboard
_Coming soon - Administrative interface_

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Roadmap

- [ ] Postgres cronjob for automated expired URL cleanup
- [ ] Comprehensive unit test coverage
- [ ] Integration test suite
- [ ] Metrics and monitoring integration
- [ ] Internationalization (i18n) support
- [ ] QR code generation for shortened URLs

---

<div style="text-align: center;">

**Made with ❤️ using Spring Boot**

[⬆ Back to Top](#linkshade)

</div>