

![build status](https://travis-ci.com/eportfolio-tech/server.svg?branch=dev)
[![Known Vulnerabilities](https://snyk.io/test/github/eportfolio-tech/server/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/dwyl/hapi-auth-jwt2?targetFile=package.json)


[Live Demo](https://dev.eportfolio.tech/)

## Dependencies
- `Spring Boot` v2.3.4

## Quick Start
### Using Docker
This project requires Redis, MongoDB and RabbitMQ. We've created `docker-compose` file
to simplify this process. Currently, there are 5 configurations available.
- `dev`: 
    + connect to an external MongoDB instance 
    + set up Redis, and RabbitMQ on customised port
    + Redis, RabbitMQ will be bind to localhost, you will need ssh tunnel to access it.
- `prod`: 
    + connect to an external MongoDB instance
    + set up Redis, and RabbitMQ on customised port
    + Redis, RabbitMQ will be bind to localhost, you will need ssh tunnel to access it.
- `local`: 
    + connect to a local MongoDB instance
    + set up Redis, MongoDB and RabbitMQ on default port
    + All services all exposed. You can access it from everywhere.
- `test`: 
    + connect to a local MongoDB instance
    + set up Redis, MongoDB and RabbitMQ on default port
    + All services all exposed. You can access it from everywhere.
- `sonarqube`: host a sonarqube instance for static code scan.

Once you've decided which environment you want to use, `cd` into that directory.
The following guide uses `dev` as an example. i.e. at `./docker-compose/dev/`

1. Fill variables in `example.env`
2. Rename `example.env` to `.env`
3. Execute `docker-compose up` to run the application in the foreground
3. Execute `docker-compose up -d` to start application in the background


The server will be listening to the port assigned if you see the following output.
```
server_1      | 2020-10-27 14:06:30.843  INFO 1 --- [           main] t.eportfolio.server.ServerApplication    : Started ServerApplication in 122.533 seconds (JVM running for 131.832)
```
You can use `http://localhost:PORT/api/swagger-ui.html` to access API documentation.

## Environment Variables

When deployed using `docker-compose`, the application read configuration from environment variables. 
- By default, running `docker-compose up` command in any subfolder(i.e. dev/local/prod/test) of the`docker-compose` directory
will use the `.env` file inside the subfolder.
- Use `--env-file` flag to specify a environment file if you don't want to use `.env`

The following snippet lists all environment variables used in this project with description.
You can also find this example in each subfolder with name `example.env`.
```
# ------- Slack -------
# Set slack hook url if you want watchtower to notice image update
SLACK_HOOK_URL=

# ------- Watchtower -------
# Path of docker configuration json. Set this to pull image from private docker registery.
DOCKER_CONFIG_PATH=

# ------- Server -------
# Port number the application will be listening
SERVER_PORT=8090

# ------- Redis -------
# Redis Configuration
# Redis host 
SPRING_REDIS_HOST=redis
# Redis port number
SPRING_REDIS_PORT=26379
# Redis password
SPRING_REDIS_PASSWORD=

# ------- Rabbit MQ -------
# Port number of rabbitmq management port
# Access the website using http://localhost:${RABBITMQ_WEB_PORT}/
RABBITMQ_WEB_PORT=15672
# RabbitMQ host 
SPRING_RABBITMQ_HOST=rabbitmq
# RabbitMQ port number
SPRING_RABBITMQ_PORT=5672
# RabbitMQ username
SPRING_RABBITMQ_USERNAME=
# RabbitMQ password
SPRING_RABBITMQ_PASSWORD=

# ------- 3rd Party API -------
# We use free images from unsplash.com to create exmaple portfoios
# Unsplash access API key
SPRING_UNSPLASH_ACCESS_KEY=
```





## Repository Structure
The following outlines structure of the repository with description.

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
├── sonarqube: SonarQube configurations
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

The following outlines the Spring Boot Application structure.
We structured the project based on layered architecture model. 


```
.
├── main
│   ├── java
│   │   └── tech
│   │       └── eportfolio
│   │           └── server
│   │               ├── ServerApplication.java: Entrypoint of the application
│   │               ├── common: contains code shared within the application like utility classes                       
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