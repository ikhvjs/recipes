# recipes

## Prerequisites

- Docker (with docker-compose)
- Java 11 SDK

## About the project

Built with SpringBoot and MySQL. MySQL is running in Docker container.

The data will be persisted in MySQL as volume is mounted.

API documentation is written from OpenAPI specification in open-api.yml.

You may display the documentation in [https://editor.swagger.io/](https://editor.swagger.io/).

## To start the project

Run the following CLI to start MySQL containers. 

One is for production (recipes_db) and another one is for integration testing (test_recipes_db).

```docker-compose
docker-compose -f docker-compose.dev up -d
```

After mysql containers are running, you may start the SpringBoot application in intellij. 

