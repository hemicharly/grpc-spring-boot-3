# GRPC SPRING BOOT 3 DEMO


This project is for educational purposes to understand how grpc works with spring boot. We implemented a simple example of a calculator.
Our objective was to test the project using the most up-to-date versions of the dependencies and explore error handling and log tracing between applications.


### 1. Required requirements

- Install Java 17
- Install Maven 3.6.3

### 2. Dependency versions

- *springframework.boot*: **3.20.0**
- *org.springdoc*: **2.3.0**
- *io.grpc*: **1.6.0**
- *io.lombok*: **1.18.30**
- *org.mapstruct*: **1.5.5.Final**
- *io.micrometer*: **compatible spring boot 3.2.0**

### 3. Running the applications

* install dependencies

    ```bash
    mvn clean install
    ```

* grpc-server

    ```bash
    cd grpc-server
    mvn spring-boot:run
    ```

* grpc-client

    ```bash
    cd grpc-client
    mvn spring-boot:run
    ```

### 4. Documentation

* grpc-client:
  * [doc-api](http://localhost:8081/swagger-doc/api)
  * [swagger-ui](http://localhost:8081/swagger-doc/index.html)
  * [actuator](http://localhost:8081/actuator)


* Diagram.

  <img src="./docs/diagram-grpc.png" alt="diagram-grpc.png">