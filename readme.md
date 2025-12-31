# Pop!x – Configuration & Build Guide

[![CI](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml/badge.svg)](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/dscap02/DependabilityProject-Popix/branch/main/graph/badge.svg)](https://codecov.io/gh/dscap02/DependabilityProject-Popix)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dscap02_DependabilityProject-Popix&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dscap02_DependabilityProject-Popix)
---

## Overview

Pop!x is a Java web application developed using **Maven** and packaged as a **WAR** file.  
The project is designed with a strong focus on **software dependability**, ensuring that the system is buildable, reproducible, and continuously verified.

The application supports:
- local builds using Maven
- containerized builds and execution using Docker and Docker Compose
- automated verification through CI/CD pipelines

---

## Project Structure (Relevant Files)

The project follows a standard Maven directory layout with additional configuration for Docker and CI/CD.  
The relevant files and directories are as follows:

```text
.
├── pom.xml                     # Maven configuration
├── Dockerfile                  # Multi-stage Docker build (Maven + Tomcat)
├── docker-compose.yml          # Application + database orchestration
├── docker/
│   └── tomcat/
│       └── ROOT.xml            # Tomcat context configuration for containerized deployment
├── src/
│   ├── main/
│   │   ├── java/               # Application source code (model, persistence, services, servlets)
│   │   └── webapp/             # JSP pages, static resources, web configuration
│   ├── test/
│   │   └── java/
│   │       └── com/
│   │           └── popx/
│   │               ├── unit/        # Unit tests
│   │               └── integration/ # Integration tests (DAO, services, servlets)
│   └── database/               # SQL initialization scripts
├── .github/
│   └── workflows/              # CI/CD pipeline configuration
└── target/                     # Build artifacts (generated)
```
---

## Requirements

### Local Build
- Java JDK 21
- Maven 3.8 or higher

### Containerized Build
- Docker
- Docker Compose

No IDE-specific configuration is required.

---

## Local Build (Without Docker)

The application can be built locally using the standard Maven lifecycle.

Build and test the application using:

```bash
mvn clean test
```

This command:
- compiles the source code
- executes all unit and integration tests
- generates test reports in target/surefire-reports

To package the application, run:

```bash
mvn clean package
```

This produces the deployable artifact:

```text
target/popix-1.0-SNAPSHOT.war
```
The generated WAR file can be deployed on a servlet container compatible with the Java EE / javax.servlet specification, such as Apache Tomcat 9.

---

## Containerized Build (Docker)

The application can be built and executed in a fully isolated and reproducible environment using Docker and Docker Compose. The project includes a multi-stage Dockerfile designed to ensure consistency between local development and containerized execution.

The **build stage** uses Maven 3.9 with OpenJDK 21 to compile the application and produce the WAR artifact. The **runtime stage** is based on Apache Tomcat 9 with OpenJDK 21 and deploys the generated WAR as the ROOT application. This approach guarantees reproducible builds and a controlled runtime environment.

The application requires runtime configuration through environment variables. For this purpose, the repository provides a template file named `.env.example`. Users must create a local `.env` file from this template; the `.env` file is intentionally excluded from version control to avoid committing sensitive data.

To prepare the environment configuration, run:

```bash
cp .env.example .env
```

Then edit the .env file and provide your own values:

```bash
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=Popix
MYSQL_USER=popix
MYSQL_PASSWORD=your_password
POPIX_DB_URL=jdbc:mysql://db:3306/Popix?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

```
Once the environment variables are configured, the Docker images can be built using:

```bash
docker-compose build
```
This command builds the application Docker image and executes the Maven build inside a controlled JDK 21 environment. To start the full application stack, run:

```bash
docker-compose up
```
This will start both the application container (Apache Tomcat) and the MySQL database container. 
The database is automatically initialized at startup using the SQL scripts located in 
```bash
src/database/01-createDB.sql 
src/database/02-InsertDB.sql.
```
 No manual database setup is required.

---

## Testing Strategy

The project adopts a multi-layered testing strategy aligned with software dependability principles:

- Unit tests for domain model components (Beans)
- Unit tests for service-layer components (Authentication and Security services)
- Integration tests for persistence layer (DAO implementations)
- Integration tests for presentation layer (Servlets)
- Database interaction testing through controlled test environments

All tests are executed automatically during the Maven build lifecycle and as part of the CI pipeline.

---

## CI/CD Compatibility

Continuous Integration is implemented using GitHub Actions.

The repository includes a CI workflow located at ```.github/workflows/ci.yml```, which is triggered on every push and pull request to the main branch, excluding documentation-only changes.

The CI pipeline:
- sets up Java 21
- executes the Maven build
- runs all automated tests
- generates code coverage data
- uploads coverage results to Codecov

The CI pipeline executes the following command:
```bash
mvn clean test package
```

Docker-based builds are supported locally as a reproducible execution environment but are not executed as part of the CI pipeline.

---

## Build Artifacts

The build process produces the following artifacts:
- WAR file: ``` target/popix-1.0-SNAPSHOT.war ```
- Test reports: ``` target/surefire-reports```
- Coverage reports generated during CI (JaCoCo XML and HTML)

---

## Summary

Pop!x is designed to be:
- buildable locally and in CI/CD environments
- reproducible through containerization
- continuously verified through automated testing
- independent from development environments

The combined use of Maven, Docker, and GitHub Actions ensures portability, reliability, and consistency across all supported execution environments, aligning the project with software dependability best practices.
