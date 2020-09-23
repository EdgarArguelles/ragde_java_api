RAGDE Java API
===============
Java REST / GraphQL API using SPQR, OAuth, JJWT, WebSocket, Spring

In this example the app uses MySQL as Data Base, but its ready to use MongoDB instead

# Live API:
[Demo page](https://ragdejavaapi.herokuapp.com)

# Stack:
- Gradle
- Spring Boot
- Spring Data
- Spring Security
- GraphQL SPQR (GraphQL Schema Publisher & Query Resolver: @Graph annotations)
- OAuth (access with Facebook and Google)
- JJWT (Java JSON Web Token)
- WebSocket / Stomp
- Project Lombok (Getter and Setter Annotations)
- Swagger (REST Documentation)
- Junit
- Jacoco (Junit Code Coverage)

# Changes
All changes must be done on dev branch, because all changes in master will deploy version.

# Disable security
In order to run application without security, in ragde.security.WebSecurityConfig.

- Comment @EnableGlobalMethodSecurity annotation.
- Change .anyRequest().authenticated() for .anyRequest().permitAll().

# Switch MySQL by Mongo
Change all Repositories: extends MongoRepository instead of MySQLRepository, for example.

- Change "RoleRepository extends MySQLRoleRepository" for "RoleRepository extends MongoRoleRepository"

# Build (create both jar and war)
    ./gradlew build
- The WAR can be deployed with any application server like Tomcat, JBoos, etc.
- The JAR can be run with Tomcat embedded server with the command: java -jar -Dspring.profiles.active=sql-local ragde_java_api-x.x.x.jar

# Run
    ./gradlew bootRun

# Run Tests
    ./gradlew test jacocoTestReport
- Generated report (build/reports/tests/test/index.html)
- Coverage (build/reports/jacoco/test/html/index.html)

# Java Documentation
    ./gradlew javadoc
- Generated report (build/docs/javadoc/index.html)

# REST Documentation
    http://localhost:8000/ragde/swagger-ui/index.html?configUrl=/ragde/v3/api-docs/swagger-config