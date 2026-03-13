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

1. create a `.env` file with the following content:
        ```
        PGUSER=YOUR_DB_USER
        PGPASSWORD=YOUR_DB_PASSWORD
        ```
2. run the database with docker compose
        ```
        docker compose up -d
        ```
3. configure the run configuration in your IDE to
   - use `local` Spring profile
   - set the environment variables from the `.env` file