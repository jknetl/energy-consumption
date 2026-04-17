# Energy consumption visualizer

This is a web application that stores and visualizes energy consumption data.

## Building

```
./gradlew build

# build docker image locally
./gradlew jibDockerBuild

# build docker image and push to registry
./gradlew jib 
```

## Running locally

1. Copy the `.env.example` to `.env` and set your credentials
2. run the database with docker compose
    ```
    docker compose up -d
    ```
3. configure the run configuration in your IDE to
   - use `local` Spring profile
   - set the environment variables from the `.env` file
4. from terminal
   - load variables from the `.env` file
   - run ` ./gradlew bootRun --args='--spring.profiles.active=local'`