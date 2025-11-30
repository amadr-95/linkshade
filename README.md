# TODO

- [x] Implement Google/GitHub Oauth
- [x] Avoid unlogged users to use the /short-urls endpoint after X times (rate limit)
- [x] Implement rate limit for logged users too
- [ ] Postgres cronjob for removing expired public urls
- [x] Check for unexpected changes in the backend. What happens if someone change the input type in the date field?, disabled buttons, etc
- [x] Avoid sending the userId to the front-end (model). Get it from the authenticationService when deleting a user
- [ ] Unit tests 
- [ ] Integration tests
- [x] Increase number of characters to the original url (~1000)
- [x] Image carrousel explaining the product 
- [x] Add links to my accounts 
- [x] Add metadata in layout 
- [x] Check responsiveness and styles
- [x] Improve logging and errorMessages
- [x] Fix discrepancy between days of expiration
- [x] Delete user account and remove all URLs linked to it
- [x] Delete all URLs when removing a user from Admin Dashboard
- [ ] Improve logging messages
- [x] Improve GlobalErrorHandler
- OPTIONAL:
- [x] Button for deleting all expired urls in my-urls/admin html page
- [x] Button for reactivating all expired urls in my-urls page 

# URL Shortener App

A URL shortening service built with Spring Boot, Java 21, and PostgreSQL.

## Description

This application allows users to shorten long URLs and track usage statistics. It supports both public and private URLs
with user management capabilities.

## Technical Requirements

- Java 21
- Maven
- Docker (PostgreSQL)
- Spring Boot 3.5.7

## Architecture

### Core Entities

The system is based on two main entities:

- `User`: Manages registered user information
- `ShortUrl`: Stores shortened URLs and their metadata

### Database Migrations with Flyway

Flyway is used to manage database migrations in a controlled and versioned manner. This tool allows:

- Maintaining a history of changes to the database structure
- Applying migrations consistently across different environments
- Avoiding conflicts in database schemas

#### Flyway Configuration

Flyway requires the following directory structure:

```
resources
|-- db
    |-- migration
        |-- V1__script_name.sql
        |-- V2__script_name.sql
        |-- ...
```

Migration scripts follow a naming convention: `V{number}__{description}.sql`.

## Performance Optimization

### Preventing the N+1 Select Problem

To avoid the N+1 Select problem (where an additional query is performed for each retrieved record), the application
sets:

```yaml
spring:
  jpa:
    open-in-view: false
```

This setting:

- Closes the EntityManager when the transaction ends (typically at the end of the controller method)
- Prevents database connections from remaining open during the entire HTTP response
- Prevents lazy loading outside the transaction context
- Improves performance by releasing database resources more quickly
- Forces explicitly loading all necessary data within the transaction

In order to use this approach, the relationships have to be marked with `fetch = FetchType.LAZY` and retrieve the necessary data
directly from the query (either using `psql` join columns or using `@EntityGraph` annotation), resulting in a single executed query by Spring Data JPA and not N+1.

## User Interface

The application uses Thymeleaf as a template engine with Bootstrap to provide a responsive and modern interface.

## Running the app

### Using Docker Compose (Recommended)

This is the recommended approach as it orchestrates all services together.

#### Option 1: Running PostgreSQL only with Docker Compose

If you want to run the Spring Boot application from your IDE and only use Docker for the database:

1. Start only the PostgreSQL service:
```bash
docker-compose up -d linkshade-db
```

2. Run the Spring Boot application from your IDE or using Maven:
```bash
./mvnw spring-boot:run
```

3. Stop the database when finished:
```bash
docker-compose down
```

#### Option 2: Running everything with Docker Compose

To run both the database and the application as containers:

1. Build and start all services in detached mode (background):
```bash
docker-compose up -d
```

2. View logs:
```bash
docker-compose logs -f
```

3. Stop all services:
```bash
docker-compose down
```

4. Stop and remove volumes (database data):
```bash
docker-compose down -v
```