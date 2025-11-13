# ClusteredData Warehouse - FX Deals Management
# Makefile for streamlined operations

.PHONY: help build test run clean docker-build docker-up docker-down docker-logs coverage install

# Default target
help:
	@echo "=================================="
	@echo "FX Deals Warehouse - Make Commands"
	@echo "=================================="
	@echo "make install        - Install dependencies"
	@echo "make build          - Build the application"
	@echo "make test           - Run unit tests"
	@echo "make coverage       - Run tests with coverage report"
	@echo "make run            - Run the application locally"
	@echo "make clean          - Clean build artifacts"
	@echo "make docker-build   - Build Docker image"
	@echo "make docker-up      - Start Docker containers"
	@echo "make docker-down    - Stop Docker containers"
	@echo "make docker-logs    - View Docker logs"
	@echo "make all            - Clean, build, test, and run Docker"
	@echo "=================================="

# Install dependencies
install:
	@echo "Installing dependencies..."
	./mvnw clean install -DskipTests

# Build the application
build:
	@echo "Building application..."
	./mvnw clean package -DskipTests

# Run unit tests
test:
	@echo "Running unit tests..."
	./mvnw test

# Run tests with coverage
coverage:
	@echo "Running tests with coverage..."
	./mvnw clean test jacoco:report
	@echo "Coverage report generated at: target/site/jacoco/index.html"

# Run the application locally
run:
	@echo "Starting application..."
	./mvnw spring-boot:run

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	./mvnw clean
	rm -rf logs/

# Build Docker image
docker-build:
	@echo "Building Docker image..."
	docker-compose build

# Start Docker containers
docker-up:
	@echo "Starting Docker containers..."
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@timeout 30
	@echo "Application is running at http://localhost:8080"
	@echo "Health check: http://localhost:8080/api/fx-deals/health"

# Stop Docker containers
docker-down:
	@echo "Stopping Docker containers..."
	docker-compose down

# View Docker logs
docker-logs:
	@echo "Viewing Docker logs..."
	docker-compose logs -f app

# Complete workflow: clean, build, test, and deploy
all: clean build test docker-build docker-up
	@echo "Complete workflow executed successfully!"
