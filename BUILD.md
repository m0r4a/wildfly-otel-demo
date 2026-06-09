# Build Guide: WAR Compilation

This document explains how to manually compile the frontend and backend `.war` files,
either using Docker (no local dependencies) or with Maven/JDK installed on the machine.

## Relevant Structure

```
.
├── java_apps/
│   ├── backend/          # BackendServlet source
│   │   ├── pom.xml
│   │   └── src/
│   └── frontend/         # FrontendServlet source
│       ├── pom.xml
│       └── src/
└── wars/                 # Compiled .war output directory
```

---

## Option A: Docker (recommended, no local dependencies)

Requires only **Docker** running. No JDK or Maven installation needed.

```bash
# Backend
docker run --rm \
  -v "${PWD}/java_apps/backend:/app" \
  -w /app \
  maven:3.6.3-jdk-8 mvn clean package

# Frontend
docker run --rm \
  -v "${PWD}/java_apps/frontend:/app" \
  -w /app \
  maven:3.6.3-jdk-8 mvn clean package

# Copy WARs to output directory
mkdir -p wars
cp java_apps/backend/target/backend.war   wars/
cp java_apps/frontend/target/frontend.war wars/
```

> **Note on the image**: `maven:3.6.3-jdk-8` pins both the Maven and JDK versions.
> WildFly 9.0.2 requires Java 8; WildFly 18.0.1 supports Java 8–11.
> If targeting WildFly 18+ with Java 11, switch the image to `maven:3.8-openjdk-11`.

## Option B: Local Maven

Requires **JDK 8+** and **Maven 3.6+** installed and available on the `PATH`.

```bash
# Verify versions before building
java -version
mvn -version

# Backend
cd java_apps/backend
mvn clean package
cd ../..

# Frontend
cd java_apps/frontend
mvn clean package
cd ../..

# Copy WARs to output directory
mkdir -p wars
cp java_apps/backend/target/backend.war   wars/
cp java_apps/frontend/target/frontend.war wars/
```

---

## Using the Makefile

The `Makefile` at the repo root automates the steps above.

| Command | Behavior |
|---|---|
| `make build` | Builds using Docker (`maven:3.6.3-jdk-8` image) |
| `make build LOCAL=1` | Builds using the locally installed Maven/JDK |
| `make clean` | Removes `target/` from both modules and the WARs in `wars/` |

```bash
# With Docker
make build

# With local tools
make build LOCAL=1

# Clean build artifacts
make clean
```

## Expected Output

After running either option, the `wars/` directory should contain:

```
wars/
├── backend.war
└── frontend.war
```

These files are mounted directly into the WildFly container by `compose.yaml`.

---

## Troubleshooting

**Build fails with `Permission denied` in Docker**
Some systems mount volumes as root. Add `--user "$(id -u):$(id -g)"` to the `docker run` command.

**`mvn: command not found` on local build**
Install Maven via your distro's package manager:
```bash
# Fedora/RHEL/Rocky
sudo dnf install maven

# Debian/Ubuntu
sudo apt install maven
```

**WildFly fails to load the WAR (deployment failed)**
Confirm the WAR was compiled with a JDK version compatible with the WildFly version in use.
Check the logs with: `docker logs wildfly-demo`.
