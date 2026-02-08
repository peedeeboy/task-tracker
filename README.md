# HMCTS Task Tracker

## Running
Execute the following a terminal in the project root:
```shell
./runstack.sh
```
this will spawn a Gradle daemon running parallel processes:
  * Backend - Kotlin/Spring Boot app on port 4000
  * Frontend - Typescript/Express app on port 3100

Open http://localhost:3100 in your browser to access the application frontend.

