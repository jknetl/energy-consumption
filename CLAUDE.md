# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.github.jknetl.ec.SomeTest"

# Run application (requires local profile + env vars, see below)
./gradlew bootRun --args='--spring.profiles.active=local'

# Build Docker image to local Docker daemon
./gradlew jibDockerBuild
```

## Local Development Setup

1. Copy `.env.example` to `.env` and fill in `PGUSER` / `PGPASSWORD`
2. Start PostgreSQL: `docker compose up -d`
3. Run with the `local` Spring profile (loads `application-local.yaml` which points at the local DB and uses `ddl-auto: create-drop`)

Tests use H2 in-memory — no external DB needed for them.

## Architecture

**Package root**: `com.github.jknetl.ec`

Three-layer structure: `data` → `service` → `rest`

### Domain Model

Multi-tenant hierarchy: `Tenant → Location → Meter → MeterReading`

All entities implement `TenantScopedEntity` (provides `getId()` + `getTenant()`). Repositories extend `TenantAwareJpaRepository` which adds `findAllByTenantId()`. Real tenant resolution is not implemented — the services use a hardcoded constant `UNIMPLEMENTED_TENANT_ID = "tenant1"` in `ControllerUtils`.

### REST Layer

Base path: `/api` (defined in `ControllerConstants`). DTOs are Java records in `rest/dto/`. MapStruct mappers live in `rest/mapper/` and are Spring-managed components.

Notable mapper quirks:
- `LocationMapper` maps DTO field `countryCode` → entity field `country`
- `MeterReadingMapper` ignores the `meter` field during mapping; the service sets it manually after mapping

### Service Layer

`ServiceUtils` provides two shared helpers: `verifyEntityHasNoId()` (guards POST endpoints) and `verifyEntityExists()` (guards PUT endpoints).

### Build Tooling

- Java 25 toolchain (Temurin in CI)
- Jib plugin for Docker images — target image: `jknetl/energy-consumption`
- Lombok + MapStruct annotation processors both configured in `build.gradle`
