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

## API Collection (Bruno)

A [Bruno](https://www.usebruno.com/) collection for manually exploring the REST API is located in [`bruno-collection/`](bruno-collection/). Open that folder in the Bruno desktop app, select the **local** environment, and run requests against a locally running instance.

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

## Deploying to Kubernetes

### Prerequisites

- `kubectl` configured for your target cluster
- `kustomize` v5+ (or `kubectl` v1.27+ with built-in kustomize)
- Docker image built and pushed: `./gradlew jib`
- [CloudNativePG operator](https://cloudnative-pg.io/) installed in the cluster — provisions the PostgreSQL cluster and credentials automatically

### Steps

1. *(Optional)* Pin a specific image tag by adding to the overlay's `kustomization.yaml`:

   ```yaml
   images:
     - name: jknetl/energy-consumption
       newTag: "1.0.0"
   ```

2. Apply:

   ```bash
   # Dev (manual):
   kubectl apply -k k8s/overlays/dev

   # Prod: managed by ArgoCD — push to main and sync the energy-consumption application.
   ```