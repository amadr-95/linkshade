# URL Shortener App

## Entities: User and ShorUrl

The best approach for production grade applications is to use `Flyway` as a database migration tool. This
dependency allows you to keep track of the changes made in the database, and it compares them between versions.
Add the `flyway-core` dependency and the one for teh database you are using. In this case:
```xml

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>

```

Flyway needs a specific folder structure to work out:

```
resources
|-- db
    |-- migration
        |-- V1__script_name.sql
        |-- V2__script_name.sql
        |-- ...
```
