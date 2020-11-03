![build status](https://travis-ci.com/eportfolio-tech/server.svg?branch=dev)
![Known Vulnerabilities](https://snyk.io/test/github/eportfolio-tech/server/badge.svg?targetFile=build.gradle)
[![Coverage Status](https://coveralls.io/repos/github/eportfolio-tech/server/badge.svg?branch=dev)](https://coveralls.io/github/eportfolio-tech/server?branch=dev)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-5-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## Forty-Two e-portfolio
This repository contains source code for backend server of Forty-Two e-portfolio platform.
This project is built for IT Project at The University of Melbourne. 
Todo: WEBSITE DESCRIPTION TO BE COMPLETED

[Live Demo](https://dev.eportfolio.tech/)


## Dependencies
- `Spring Boot` v2.3.4
- `Gradle` 6.4.1
- `Redis` 6.0.9
- `MongoDB` 4.4.1
- `RabbitMQ` 3.8.9

## Quick Start
### Using Docker
This project requires Redis, MongoDB and RabbitMQ. We've created `docker-compose` configurations to simplify this process. 
![](https://github.com/eportfolio-tech/server/blob/dev/diagram/docker-compose-diagram/docker-compose.png?raw=true)
- `dev`: 
    + connect to an external MongoDB instance 
    + run Redis, and RabbitMQ on customised port
    + Redis, RabbitMQ will be bind to localhost, you will need ssh tunnel to access it.
- `prod`: 
    + connect to an external MongoDB instance
    + run Redis, and RabbitMQ on customised port
    + Redis, RabbitMQ will be bind to localhost, you will need ssh tunnel to access it.
- `local`: 
    + connect to a local MongoDB instance
    + run Redis, MongoDB and RabbitMQ on default port
- `test`: 
    + connect to a local MongoDB instance
    + run Redis, MongoDB and RabbitMQ on default port

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

## Configuration

The server read configuration such as database connection from environment variables.

- By default, running `docker-compose up` command in any subfolder(i.e. dev/local/prod/test) of the`docker-compose` directory
will use the `.env` file inside the subfolder.
- Use `--env-file` flag to specify an environment file if you don't want to use `.env`

The following snippet lists all environment variables used in this project with description.
You can also find this example in each subfolder with name `example.env`.
```
# ------- Slack -------
# Spring profile
PROFILE=local

# Set slack hook url if you want watchtower to notice image update
SLACK_HOOK_URL=

# ------- Watchtower -------
# Path of docker configuration json. Set this to pull image from private docker registry.
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

# ------- Mongo DB -------
# specify database to use
SPRING_MONGO_DB_NAME=
# MongoDB connection string. It should start with mongodb+srv:// or mongodb://
SPRING_MONGO_CONNECTION_STRING=

# ------- Azure -------
# Azure storage account connection string
SPRING_AZURE_STORAGE_CONNECTION_STRING=
# default container name
SPRING_AZURE_STORAGE_CONTAINER_NAME=

# ------- 3rd Party API -------
# We use free images from unsplash.com to create example portfolio
# Unsplash access API key
SPRING_UNSPLASH_ACCESS_KEY=
```

## Local Development
1. run `docker-compose -f ./docker-compose/local/docker-compose.yml up -d` 
2. run `cp ./docker-compose/local/example.env ./docker-compose/local/.env`
3. fill the `.env` file at `./docker-compose/local/.env`
3. run `export $(cat ./docker-compose/local/.env | xargs)` to export environment variables to active shell. Alternatively, you can use [`EnvFile`](https://plugins.jetbrains.com/plugin/index?xmlId=net.ashald.envfile) for IntelliJ IDEA to mange environment variables.
4. run `./gradlew bootRun` on *unix or `.\gradlew.bat bootRun` on Windows. Note that it might take a while to download dependencies.
The server will be listening on port 8090 by default. 
You can use `http://localhost:PORT/api/swagger-ui.html` to access API documentation.

## Testing
This project has implemented unit testing for API endpoints.
1. run `docker-compose -f ./docker-compose/test/docker-compose.yml up -d` 
2. run `cp ./docker-compose/test/example.env ./docker-compose/test/.env `
3. run `export $(cat ./docker-compose/test/.env | xargs)` to export environment variables to active shell
4. run `./gradlew test` on *unix or `.\gradlew.bat test` on Windows. Note that it might take a while to download dependencies.

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


## Architecture
### Overview
![](https://github.com/eportfolio-tech/server/blob/dev/diagram/architecture-diagram.png?raw=true)
The following outlines the Spring Boot Application structure. We structured the project based on a layered architecture model.
### Spring Boot Structure

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

## Message Queue Usage
![](https://github.com/eportfolio-tech/server/blob/dev/diagram/message-queue.png?raw=true)

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://www.linkedin.com/in/yunfeijing/"><img src="https://avatars3.githubusercontent.com/u/18676002?v=4" width="100px;" alt=""/><br /><sub><b>Yunfei Jing</b></sub></a><br /><a href="https://github.com/eportfolio-tech/server/commits?author=yunfeijing" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Skylar-Yang"><img src="https://avatars1.githubusercontent.com/u/61859437?v=4" width="100px;" alt=""/><br /><sub><b>Skylar-Yang</b></sub></a><br /><a href="https://github.com/eportfolio-tech/server/commits?author=Skylar-Yang" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Zijian-Zeng"><img src="https://avatars3.githubusercontent.com/u/53477823?v=4" width="100px;" alt=""/><br /><sub><b>Zijian-Zeng</b></sub></a><br /><a href="https://github.com/eportfolio-tech/server/commits?author=Zijian-Zeng" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/shuyang-fan-33231a17b/"><img src="https://avatars3.githubusercontent.com/u/34025121?v=4" width="100px;" alt=""/><br /><sub><b>Haswell Fan</b></sub></a><br /><a href="https://github.com/eportfolio-tech/server/commits?author=Haswf" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/FishWith7sMemory"><img src="https://avatars0.githubusercontent.com/u/63847386?v=4" width="100px;" alt=""/><br /><sub><b>FishWith7sMemory</b></sub></a><br /><a href="https://github.com/eportfolio-tech/server/commits?author=FishWith7sMemory" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!