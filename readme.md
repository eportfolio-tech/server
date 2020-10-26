
## Repository Structure

```
.
├── diagram: diagrams
├── docker-compose: docker-compose files to spin up the server in a second
│   ├── redis.conf: Redis configuration
│   ├── example.env: an empty .env file which you need to fill
│   ├── dev
│   │   └── docker-compose.yml
│   ├── local
│   │   └── docker-compose.yml
│   ├── prod
│   │   └── docker-compose.yml
│   └── test
│       └── docker-compose.yml
├── readme.md
├── server.plantuml 
├── sonarqube: SonarQue configurations
│   └── sonar.properties
├── sonarqube.yml: docker-file to set up SonarQube instance
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── src: source code
    ├── main: see Spring Boot Project Structure below

```


## Spring Boot Project Structure

```
.
├── main
│   ├── java
│   │   └── tech
│   │       └── eportfolio
│   │           └── server
│   │               ├── ServerApplication.java: Entrypoint of the application
│   │               ├── common                              
│   │               ├── config: Spring Boot Configuration Classes 
│   │               ├── controller: controllers
│   │               ├── dto: POJOs that carries data between the client and the server
│   │               ├── interceptor: interceptors
│   │               ├── job: Quartz jobs
│   │               ├── listener: Event listeners
│   │               ├── model: Domain models 
│   │               ├── repository: Spring Data Repositories
│   │               ├── security: Spring security configuration
│   │               ├── server.plantuml: UML of the server package
│   │               └── service: Service interfaces and implementation                     
│   └── resources: Spring properties
│       ├── application-dev.yml: properties for development environment                            
│       ├── application-local.yml: properties for local environment 
│       ├── application-prod.yml: properties for production environment  
│       └── application-test.yml: properties for testing environment
└── test: testcases
    └── java
        └── tech
            └── eportfolio
                └── server
                    ├── Swagger2MarkupTest.java
                    ├── controller: controller tests
                    ├── listener: listener tests
                    └── service: service tests
```