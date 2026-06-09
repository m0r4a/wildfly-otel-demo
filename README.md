# OpenTelemetry Java Agent Demo — WildFly

Automatic instrumentation demo using the **OpenTelemetry Java Agent** on WildFly, comparing agent behavior across two versions of the application server:
**WildFly 9.0.2.Final** (legacy) and **WildFly 18.0.1.Final**.

## What does this project demonstrate?

The OpenTelemetry Java Agent (`opentelemetry-javaagent.jar`) instruments Java applications
**without any source code changes**. It attaches to the JVM at WildFly startup and automatically
captures traces, metrics, and logs from:

- Inbound and outbound HTTP requests (servlets, HTTP clients)
- Database queries via JDBC
- Inter-service communication (frontend → backend → PostgreSQL)

This repo includes two simple servlets that simulate that call chain, making it easy to observe
agent behavior across both WildFly versions.

## Architecture

```
Browser
  │
  ▼
[WildFly] frontend.war (/dashboard)
  │  HTTP → localhost:8080/backend/api/data
  ▼
[WildFly] backend.war (/api/data)
  │  JDBC
  ▼
[PostgreSQL] demo_db.clientes
```

All traffic is intercepted by the Java Agent and exported via OTLP to the collector:

```
WildFly (javaagent) ──OTLP/gRPC──► OTel Collector ──► Jaeger      (traces)
                                                    └──► Prometheus  (metrics)
                                                               │
                                                           Grafana   (dashboards)
```

## Stack

| Component | Image / Version |
|-----------|-----------------|
| WildFly (demo v9) | `jboss/wildfly:9.0.2.Final` |
| WildFly (demo v18) | `jboss/wildfly:18.0.1.Final` |
| PostgreSQL | `postgres:13-alpine` |
| OTel Collector | `otel/opentelemetry-collector:latest` |
| Prometheus | `prom/prometheus:latest` |
| Jaeger | `jaegertracing/all-in-one:latest` |
| Grafana | `grafana/grafana:latest` |

## Requirements

- **Docker** and **Docker Compose** (v2+)
- **Make** (optional, for building the WARs)
- **JDK 8** + **Maven 3.6+** (only if building locally without Docker)

## Quick Start

### 1. Get the WARs

**Option A: Download pre-built WARs (no build tooling required)**

```bash
mkdir -p wars
curl -Lo wars/backend.war  https://github.com/m0r4a/wildfly-otel-demo/releases/download/v0.0.1/backend.war
curl -Lo wars/frontend.war https://github.com/m0r4a/wildfly-otel-demo/releases/download/v0.0.1/frontend.war
```

Or download them manually from the [releases page](https://github.com/m0r4a/wildfly-otel-demo/releases/tag/v0.0.1) and place them in the `wars/` directory.

**Option B: Build from source**

```bash
# Using Docker (no local JDK required)
make build

# Using local Maven
make build LOCAL=1
```

See [BUILD.md](./BUILD.md) for detailed instructions and troubleshooting.

### 2. Download the Java Agent

`opentelemetry-javaagent.jar` is **not included in the repo** due to its size.

Download it from the official releases page:

```bash
curl -Lo opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

Or visit: https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases

### 3. Start the stack

```bash
docker compose up -d
```

### 4. Verify the deployment

```bash
# Wait for "Deployed backend.war" and "Deployed frontend.war" in the logs
docker logs -f wildfly-demo
```

### 5. Access the services

| Service | URL |
|---------|-----|
| Frontend (dashboard) | http://localhost:8080/frontend/dashboard |
| WildFly Admin Console | http://localhost:9990 |
| Grafana | http://localhost:3000 |
| Jaeger UI | http://localhost:16686 |
| Prometheus | http://localhost:9090 |

## Java Agent Configuration

The agent is configured via `JAVA_OPTS` in `compose.yaml`:

```
-javaagent:/opt/jboss/wildfly/opentelemetry-javaagent.jar
-Dotel.service.name=wildfly-cluster
-Dotel.exporter.otlp.endpoint=http://otel-collector:4317
-Dotel.exporter.otlp.protocol=grpc
-Dotel.metrics.exporter=otlp
-Dotel.traces.exporter=otlp
-Dotel.logs.exporter=none
```

> **Note**: To switch between WildFly versions, edit the image and the `JAVA_OPTS` in `compose.yaml`:

> **Note**: WildFly 9 runs on Java 8 and requires `XX:MaxPermSize` (PermGen).
> WildFly 18+ targets Java 11+ with Metaspace; remove that flag from `JAVA_OPTS` when switching images.

## Database

PostgreSQL is automatically initialized with the `customers` table and sample data.
The initialization script is at `configs/postgres-init.sql`.

Direct connection (debug):
```bash
docker exec -it postgres-db psql -U demo_user -d demo_db
```

## Observability

### Traces: Jaeger

Open http://localhost:16686, select the `wildfly-cluster` service, and search for traces.

Each request to `/frontend/dashboard` produces a trace spanning the servlet, the HTTP call
to the backend, and the JDBC query to PostgreSQL.

### Metrics: Grafana

Open http://localhost:3000. Data sources (Prometheus + Jaeger) are pre-provisioned.
Create a dashboard or import one from the Grafana dashboard repository for JVM / WildFly.

### Available JVM metrics (via OTel)

- `jvm.memory.used` / `jvm.memory.max`
- `jvm.gc.duration`
- `jvm.thread.count`
- `http.server.request.duration` (HTTP latency histogram)

## Stopping the Stack

```bash
docker compose down

# Also remove the PostgreSQL volume
docker compose down -v
```
