.PHONY: help build test clean run

help:
	@echo "Available commands:"
	@echo "  make build        - Build the application"
	@echo "  make test         - Run tests"
	@echo "  make clean        - Clean build artifacts"
	@echo "  make run          - Run application locally"

build:
	./gradlew clean build

test:
	./gradlew clean test

clean:
	./gradlew clean

run:
	docker start linkshade-db
	set -a && source .env.local && set +a && ./gradlew bootRun
