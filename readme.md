# Pop!x – Configuration & Build Guide

[![CI](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml/badge.svg)](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/dscap02/DependabilityProject-Popix/branch/main/graph/badge.svg)](https://codecov.io/gh/dscap02/DependabilityProject-Popix)

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
│   │               ├── unit/   # Unit tests
│   │               └── integration/ # Integration tests (DAO, services, servlets)
│   └── database/               # SQL initialization scripts
├── .github/
│   └── workflows/              # CI/CD pipeline configuration
└── target/                     # Build artifacts (generated)
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

mvn clean test

This command:
- compiles the source code
- executes all unit and integration tests
- generates test reports in target/surefire-reports

To package the application, run:

mvn clean package

This produces the deployable artifact:

target/popix-1.0-SNAPSHOT.war

The generated WAR file can be deployed on a servlet container compatible with the Java EE / javax.servlet specification, such as Apache Tomcat 9.

---

## Containerized Build (Docker)

The application can also be built and executed in a fully isolated environment using Docker.

The project includes a multi-stage Dockerfile:

Build stage:
- Maven 3.9
- OpenJDK 21
- Compiles the application and produces the WAR artifact

Runtime stage:
- Apache Tomcat 9
- OpenJDK 21
- Deploys the WAR as the ROOT application

This approach ensures reproducible builds and consistency across local and containerized environments.

To build the containers:

docker compose build

This step builds the application Docker image and executes the Maven build inside a controlled JDK 21 environment.

To run the application:

docker compose up

This will start:
- the application container (Apache Tomcat)
- the MySQL database container

The database is automatically initialized using the SQL scripts located in:

src/database/01-createDB.sql  
src/database/02-InsertDB.sql

No manual database setup is required.

---

## Environment Configuration

Environment variables are used to configure the database connection and are injected at runtime through Docker Compose.

Example variables:
- MYSQL_ROOT_PASSWORD
- MYSQL_USER
- MYSQL_PASSWORD
- MYSQL_DATABASE

This configuration approach allows the same setup to be reused consistently across local and containerized environments.

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

The repository includes a CI workflow located at .github/workflows/ci.yml, which is triggered on every push and pull request to the main branch, excluding documentation-only changes.

The CI pipeline:
- sets up Java 21
- executes the Maven build
- runs all automated tests
- generates code coverage data
- uploads coverage results to Codecov

The CI pipeline executes the following command:

mvn clean test package

Docker-based builds are supported locally as a reproducible execution environment but are not executed as part of the CI pipeline.

---

## Build Artifacts

The build process produces the following artifacts:
- WAR file: target/popix-1.0-SNAPSHOT.war
- Test reports: target/surefire-reports
- Coverage reports generated during CI (JaCoCo XML and HTML)

---

## Summary

Pop!x is designed to be:
- buildable locally and in CI/CD environments
- reproducible through containerization
- continuously verified through automated testing
- independent from development environments

The combined use of Maven, Docker, and GitHub Actions ensures portability, reliability, and consistency across all supported execution environments, aligning the project with software dependability best practices.
