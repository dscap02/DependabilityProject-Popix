# Pop!x – Configuration & Build Guide
[![CI](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml/badge.svg)](https://github.com/dscap02/DependabilityProject-Popix/actions/workflows/ci.yml)


## Overview

Pop!x is a Java web application developed using **Maven** and packaged as a **WAR** file.  
The project is designed to be **buildable and executable both locally and in CI/CD environments**, using standard and reproducible tooling.

The application supports:
- local builds using Maven
- containerized builds and execution using Docker and Docker Compose

---

## Project Structure (Relevant Files)

    .
    ├── pom.xml                 # Maven configuration
    ├── Dockerfile              # Multi-stage Docker build (Maven + Tomcat)
    ├── docker-compose.yml      # Application + database orchestration
    ├── src/
    │   ├── main/               # Application source code
    │   ├── test/               # Automated tests
    │   └── database/           # SQL initialization scripts
    └── target/                 # Build artifacts (generated)

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

### Build and Test

    mvn clean test

This command:
- compiles the source code
- executes all automated tests
- generates test reports in `target/surefire-reports`

### Package

    mvn clean package

This command produces the deployable artifact:

    target/popix-1.0-SNAPSHOT.war

The generated WAR file can be deployed on a servlet container compatible with the **Java EE / javax.servlet** specification, such as **Apache Tomcat 9**.

---

## Containerized Build (Docker)

The application can also be built and executed in a fully isolated environment using Docker.

### Dockerfile Overview

The project includes a **multi-stage Dockerfile**:

- **Build stage**
    - Maven 3.9
    - OpenJDK 21
    - Compiles the application and produces the WAR artifact

- **Runtime stage**
    - Apache Tomcat 9
    - OpenJDK 21
    - Deploys the WAR as the ROOT application

This approach ensures reproducible builds and consistency across local and containerized environments.

### Build Containers

    docker compose build

This step:
- builds the application Docker image
- executes the Maven build inside a controlled JDK 21 environment

### Run Application

    docker compose up

This will start:
- the application container (Apache Tomcat)
- the MySQL database container

The database is automatically initialized using the SQL scripts located in:

    src/database/
    ├── 01-createDB.sql
    └── 02-InsertDB.sql

No manual database setup is required.

---

## Environment Configuration

Environment variables are used to configure the database connection and are injected at runtime through Docker Compose.

Example variables:
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_DATABASE`

This configuration approach allows the same setup to be reused locally and in containerized environments.

---

## CI/CD Compatibility

Continuous Integration is implemented using **GitHub Actions**.

The repository includes a CI workflow (`.github/workflows/ci.yml`) that is triggered on every push and pull request to the `main` branch.  
The pipeline:
- sets up **Java 21**
- runs the standard Maven build
- executes automated tests
- produces the WAR artifact

The CI pipeline runs the following command:

    mvn clean test package

Docker-based builds are supported locally as an alternative, reproducible build method, but they are not currently executed as part of the CI pipeline.

---

## Build Artifacts

- WAR file:  
  `target/popix-1.0-SNAPSHOT.war`

- Test reports:  
  `target/surefire-reports/`

These artifacts can be archived or published by a CI/CD pipeline.

---

## Summary

Pop!x is designed to be:
- buildable locally
- buildable in CI/CD pipelines
- reproducible and automated
- independent from development environments

The use of Maven, Docker, and GitHub Actions ensures portability, reliability, and consistency across all supported execution environments.
