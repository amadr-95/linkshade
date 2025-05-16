# URL Shortener App

A URL shortening service built with Spring Boot, Java 21, and PostgreSQL.

## Description

This application allows users to shorten long URLs and track usage statistics. It supports both public and private URLs
with user management capabilities.

## Technical Requirements

- Java 21
- Maven
- Docker (PostgreSQL)
- Spring Boot 3.4.4

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

This configuration is considered a best practice for production applications, although the default value in Spring Boot
is `true`. In order to use this approach, the relationships have to be marked with `fetch = FetchType.LAZY` and retrieve the necessary data
directly from the query (either using `psql` join columns or using `@EntityGraph` annotation), resulting in a single executed query by Spring Data JPA and not N+1.

## User Interface

The application uses Thymeleaf as a template engine with Bootstrap to provide a responsive and modern interface.

# Security topics
## Detailed explanation about CSRF in Spring Security

### What is CSRF?

CSRF (Cross-Site Request Forgery) is a type of attack where a malicious site tricks an authenticated user's browser into sending an unwanted request to your web application. The attacker takes advantage of the fact that session cookies are automatically sent with each request to the same domain.

### CSRF Configurations in Spring Security

#### 1. Completely disable CSRF

```
.csrf(CsrfConfigurer::disable)
```

**When to use it:**
- Stateless RESTful APIs
- Applications using JWT tokens or other token-based authentication mechanisms (not cookies)
- Mobile applications
- Internal services where CSRF is not a threat

#### 2. Default configuration

```
.csrf(Customizer.withDefaults())
```

**When to use it:**
- Traditional web applications with forms
- Applications using session-based authentication
- When you need protection against CSRF with minimal configuration

### Recommendations

1. For web applications with forms and session authentication: **enable CSRF**
2. For pure REST APIs using JWT or stateless authentication: **disable CSRF**
3. For hybrid applications: **custom configuration** that protects vulnerable parts

## Disabling anonymous authentication in Spring Security

In Spring Security, by default, when a user is not authenticated, the framework automatically creates an authentication object with an "anonymous" user. This behavior is managed by the `AnonymousAuthenticationFilter`.

### What is anonymous authentication?

When a user is not authenticated, Spring Security creates an anonymous authentication token (`AnonymousAuthenticationToken`) in the security context. This ensures that there is always an authentication object available, even if the user has not logged in.

### Why disable it?

It may be necessary to disable this behavior when:

- You need to clearly distinguish between authenticated and unauthenticated users
- You want `SecurityContextHolder.getContext().getAuthentication()` to return `null` for unauthenticated users
- You want to implement your own logic to handle unauthenticated users

### How to disable anonymous authentication

To disable anonymous authentication in Spring Security, add the following configuration:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // Rest of the configuration...
            
            // Disable anonymous authentication
            .anonymous(AbstractHttpConfigurer::disable);
            
    return http.build();
}
```
Note: disabling anonymous authentication will lead to `sec:authorize="isAnonymous()` and `sec:authorize="isAuthenticated()` 
in index.html to not work properly, because it is always expecting some authentication (anonymous or not) to show/not show the different navbar links.

### Implications

When disabling anonymous authentication:

- `SecurityContextHolder.getContext().getAuthentication()` will return `null` when there is no authenticated user
- You will need to explicitly check if authentication is `null` before accessing its properties
- Methods like `isAuthenticated()` won't work if authentication is `null`

This configuration gives you greater control over how to handle unauthenticated users in your application.