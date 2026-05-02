# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.


## Project specific information

### Commands

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

### API Collection

A [Bruno](https://www.usebruno.com/) collection lives in `bruno-collection/`. Open it in the Bruno desktop app, select the **local** environment, and issue requests against a running instance. See `bruno-collection/README.md` for details.

### Local Development Setup

1. Copy `.env.example` to `.env` and fill in `PGUSER` / `PGPASSWORD`
2. Start PostgreSQL: `docker compose up -d`
3. Run with the `local` Spring profile (loads `application-local.yaml` which points at the local DB and uses `ddl-auto: create-drop`)

Tests use H2 in-memory — no external DB needed for them.

### Architecture

**Package root**: `com.github.jknetl.ec`

Three-layer structure: `data` → `service` → `rest`

#### Domain Model

Multi-tenant hierarchy: `Tenant → Location → Meter → MeterReading`

All entities implement `TenantScopedEntity` (provides `getId()` + `getTenant()`). Repositories extend `TenantAwareJpaRepository` which adds `findAllByTenantId()`. Real tenant resolution is not implemented — the services use a hardcoded constant `UNIMPLEMENTED_TENANT_ID = "tenant1"` in `ControllerUtils`.

#### REST Layer

Base path: `/api` (defined in `ControllerConstants`). DTOs are Java records in `rest/dto/`. MapStruct mappers live in `rest/mapper/` and are Spring-managed components.

Notable mapper quirks:
- `LocationMapper` maps DTO field `countryCode` → entity field `country`
- `MeterReadingMapper` ignores the `meter` field during mapping; the service sets it manually after mapping

#### Service Layer

`ServiceUtils` provides two shared helpers: `verifyEntityHasNoId()` (guards POST endpoints) and `verifyEntityExists()` (guards PUT endpoints).

#### Build Tooling

- Java 25 toolchain (Temurin in CI)
- Jib plugin for Docker images — target image: `jknetl/energy-consumption`
- Lombok + MapStruct annotation processors both configured in `build.gradle`

### Testing Conventions

**Naming pattern:** `methodName_whenCondition_shouldExpectedBehavior`

Examples:
- `create_whenEntityHasId_shouldThrowRuntimeException`
- `findAll_whenTenantHasLocations_shouldReturnOnlyTenantLocations`
- `getAll_whenNoLocations_shouldReturn200WithEmptyList`

**Arrange/Act/Assert:** Separate the three blocks with a blank line. Do NOT write `// Arrange`, `// Act`, or `// Assert` comments.

**Parameterized tests:** Use `@EnumSource` for enum variants, `@ValueSource` for primitives/strings.

### Kubernetes Deployment

Manifests live in `k8s/` using Kustomize.

**Directory layout:**
- `k8s/base/` — Deployment, Service, PDB, ConfigMapGenerator
- `k8s/components/ingress/` — optional Traefik Ingress (opt-in per overlay)
- `k8s/overlays/dev/` — dev resources (100m–300m CPU, 256–512Mi RAM), no Ingress
- `k8s/overlays/prod/` — prod resources (250m–500m CPU, 512Mi–1Gi RAM), Ingress enabled

**Apply an overlay:**
```bash
kubectl apply -k k8s/overlays/dev
kubectl apply -k k8s/overlays/prod
```

**Secrets:** Each overlay has a committed `secrets.env.example` with `PGUSER` and `PGPASSWORD`. Copy it to `secrets.env` (gitignored) and fill in values before deploying.

**Image tag:** Update `images.newTag` in the overlay's `kustomization.yaml` after building and pushing with `./gradlew jib`.

**Database URL:** Defaults to `postgresql:5432` (in-cluster service name). Override the `SPRING_DATASOURCE_URL` literal in `k8s/base/kustomization.yaml` if your PostgreSQL service has a different name.
