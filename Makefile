MAVEN_IMAGE   := maven:3.6.3-jdk-8
WARS_DIR      := ./wars
BACKEND_SRC   := ./java_apps/backend
FRONTEND_SRC  := ./java_apps/frontend

.PHONY: help
help:
	@echo "Usage:"
	@echo "  make build          Build WAR files using Docker (Maven image)"
	@echo "  make build LOCAL=1  Build WAR files using local Maven/JDK"
	@echo "  make clean          Remove compiled artifacts from wars/ and target/ dirs"

.PHONY: build
build:
	@mkdir -p $(WARS_DIR)
ifdef LOCAL
	@echo "[local] Building backend"
	cd $(BACKEND_SRC) && mvn clean package -q
	@echo "[local] Building frontend"
	cd $(FRONTEND_SRC) && mvn clean package -q
else
	@echo "[docker] Building backend"
	docker run --rm \
		-v "$(PWD)/$(BACKEND_SRC):/app" \
		-w /app \
		$(MAVEN_IMAGE) mvn clean package -q
	@echo "[docker] Building frontend"
	docker run --rm \
		-v "$(PWD)/$(FRONTEND_SRC):/app" \
		-w /app \
		$(MAVEN_IMAGE) mvn clean package -q
endif
	@echo "Copying WARs to $(WARS_DIR)..."
	cp $(BACKEND_SRC)/target/backend.war   $(WARS_DIR)/backend.war
	cp $(FRONTEND_SRC)/target/frontend.war $(WARS_DIR)/frontend.war
	@echo "Done. WARs available in $(WARS_DIR)/"

.PHONY: clean
clean:
	@echo "Cleaning build artifacts"
	rm -rf $(BACKEND_SRC)/target $(FRONTEND_SRC)/target
	rm -f  $(WARS_DIR)/backend.war $(WARS_DIR)/frontend.war
	@echo "Clean complete."
