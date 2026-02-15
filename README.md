# Task Tracker
Task Tracker application for HM Courts & Tribunals Service technical test.

![Task Tracker Screenshot](/docs/screenshot.jpg)

## Getting started
Clone this repo and run:
```shell
./gradlew runStack
```

to stand up the entire application stack. This will spawn two Gradle parallel process running:
* **Backend**: http://localhost:4000
* **Frontend**: https://localhost:3100

Navigate to https://localhost:3100 in your browser to view / interact with the application.

## Running Tests
### Backend
```shell
./gradlew :backend:check
```
will run Unit and Integration Tests.

### Frontend
```shell
./gradlew :frontend:yarnTest
```
will run Unit Tests.

## Technology Stack
### Backend
* **Language**: Kotlin
* **Framework**: Spring Boot
* **Server**: Tomcat
* **Database**: h2 in-memory
* **Testing**: Kotest + Mockk
* **Build**: Gradle with Kotlin DSL

h2 console is available for debugging at http://localhost:4000/h2-console
* **Username**: `name`
* **Password**: `password`

### Pre-requisites
* Java 21
* Gradle (wrapper included)

### Frontend
* **Language**: Typescript
* **Framework**: Express
* **Server**: Node
* **Testing**: Jest
* **Build**: Gradle (`node-gradle-plugin`) with NPM + Yarn

### Pre-requisites
* Node 24
* NPM
* Yarn

The Node Gradle plugin should download a suitable version of Node for local development during project build.

## Future enhancements
Things I would have improved upon if time had allowed:

* End-to-End testing with Playwright
* OpenAPI docs for backend
* Form validation on frontend
* More comprehensive testing of frontend
* Fontend views of Completed / Outstanding / Almost Due tasks
* Sorting / filtering of tasks
* Enum + validation of Task status