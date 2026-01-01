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

In addition to functional and integration testing, the project also includes
a dedicated performance evaluation phase based on microbenchmarking,
which is documented separately.

---

# Performance Evaluation with JMH Microbenchmarks

Beyond functional correctness and test effectiveness, Pop!x includes a performance-oriented evaluation  
based on JMH (Java Microbenchmark Harness).

JMH is used to analyze CPU-bound and frequently executed logic in isolation from the web container,  
network stack, and database I/O, ensuring accurate and reproducible measurements.

## Benchmarked areas include:
- Cart subtotal calculation  
  Comparison between a traditional for loop and Java Streams across varying cart sizes.
- Catalog filtering logic  
  Evaluation of filtering strategies (loop vs streams) as catalog size grows.
- Security-related cryptographic operations  
  Measurement of BCrypt password hashing and verification costs for different password lengths.

Each benchmark is parameterized to simulate realistic workload growth and executed using  
JMH warm-up, measurement, and statistical aggregation mechanisms.

## Benchmark Execution Scope
Benchmarks are executed locally as a dedicated evaluation step.  
They are not part of the Maven test phase.  
They are not executed in CI/CD.

This separation is intentional:
- microbenchmarks are highly sensitive to hardware and execution environment
- CI environments are unsuitable for reliable performance measurements
- keeping performance evaluation separate avoids polluting functional test metrics

Benchmark results are exported as CSV files, which represent the authoritative data source.

---

## Published Analysis Artifacts
All analysis artifacts are published via GitHub Pages to ensure transparency,  
reproducibility, and external inspection.

Published artifacts include:
- JaCoCo code coverage reports
- PiTest mutation testing reports
- JMH microbenchmark results  


JMH results are preserved in CSV format as primary artifacts and rendered client-side  
as HTML tables for readability.

📎 Full reports are available at:  
https://dscap02.github.io/DependabilityProject-Popix/

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
- Test reports: ```target/surefire-reports```
- Coverage reports generated during CI (JaCoCo XML and HTML)

---

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=dscap02_DependabilityProject-Popix)
## SonarCloud Code Quality Analysis


The project integrates **SonarCloud** to continuously assess code quality attributes such as **reliability**, **security**, **maintainability**, **code duplication**, and **test coverage**.

SonarCloud analysis is executed through a dedicated GitHub Actions workflow located at: ``` .github/workflows/ci-sonar.yml```



This workflow is triggered **only after the main CI pipeline (build and test) completes successfully**, ensuring that static analysis is performed exclusively on buildable and tested code. This separation allows build correctness and code quality to be evaluated independently, in line with software dependability best practices.

---

### Quality Gate and Coverage Considerations

The SonarCloud Quality Gate configured for this project includes a default threshold of **80% test coverage on New Code**. At the current stage, the Quality Gate may fail due to coverage below this threshold, even though:

* **Security vulnerabilities:** None open.
* **Security hotspots:** All resolved.
* **Reliability rating:** A.
* **Maintainability rating:** A.
* **Code duplication:** Kept below 5%.

<blockquote>
  <p><strong>⚠️ IMPORTANT</strong></p>
  <p>This behavior is expected and reflects the <strong>intrinsic difficulty of achieving very high coverage in servlet-based web applications</strong>, where significant portions of logic depend on container-managed components (HTTP requests, sessions, responses) and integration behavior rather than pure unit-level execution.</p>
  <p>The Quality Gate failure <strong>does not indicate build or functional failure</strong>:</p>
  <ul>
    <li>The main CI pipeline completes successfully.</li>
    <li>All automated tests pass.</li>
    <li>The application is fully buildable and deplo</li>
  </ul>
</blockquote>


For this reason, SonarCloud is used primarily as a **code quality reporting and analysis tool**, rather than as a hard blocking mechanism for the CI pipeline.

---

### Transparency and Dependability Perspective

Rather than weakening the Quality Gate or artificially inflating coverage metrics, the project intentionally preserves SonarCloud’s default rules to provide a **realistic and transparent view of code quality trade-offs**.

This approach aligns with the goals of software dependability by:
1.  **Prioritizing correctness**, robustness, and test validity over metric gaming.
2.  **Clearly documenting limitations** and design choices.
3.  **Maintaining traceability** between CI results, test strategy, and static analysis outcomes.
## Summary

Pop!x is designed to be:
- buildable locally and in CI/CD environments
- reproducible through containerization
- continuously verified through automated testing
- independent from development environments

The combined use of Maven, Docker, and GitHub Actions ensures portability, reliability, and consistency across all supported execution environments, aligning the project with software dependability best practices.

## License

This project is developed for academic purposes.
No license is granted for reuse or redistribution.

