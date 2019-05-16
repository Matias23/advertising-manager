# MS ADVERTISE #

Microservice to interact with advertises

# Development

You will need:

- Java 8, at least `java version "1.8.0_131"`
- Docker

**NOTE (for Windows users)**: For all `./gradlew` commands, set the `PROFILE_DB_HOST` environment variable to the IP that the Docker instance is running.

e.g. If you see in the Docker terminal: 
```
docker is configured to use the default machine with IP 192.168.99.100".
```
You should set {PROFILE_DB_HOST:localhost} to {PROFILE_DB_HOST:192.168.99.100}, and so on.


**NOTE**: In case you want to completely reset the Docker instance of the database, run
```
$ docker-compose down -v
```

## Testing

1. Run the docker instance from the repository directory
   ```
   $ docker-compose -f docker-compose.yml -f docker-compose.override.yml up
   ```
2. Start the tests from the repository directory with
   ```
   $ ./gradlew clean check
   ```

## Starting the server

1. Run the docker instance from the repository directory
   ```
   $ docker-compose -f docker-compose.yml -f docker-compose.override.yml up
   ```
2. Start the server from the repository directory with
   ```
   $ ./gradlew bootRun
   ```
3. This will expose the server in `localhost:9000/ms-advertise`

## Expose the documentation

1. Run the docker instance from the repository directory
   ```
   $ docker-compose -f docker-compose.yml -f docker-compose.override.yml up
   ```
2. Start the server from the repository directory with
   ```
   $ SPRING_PROFILES_ACTIVE=doc ./gradlew bootRun
   ```
3. This will expose the server in the same place as a normal start and also expose the documentation in `http://localhost:9000/ms-advertise/swagger-ui.html`
