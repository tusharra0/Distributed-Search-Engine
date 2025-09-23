#!/bin/bash

# Distributed Search Engine Startup Script

echo "Starting Distributed Search Engine..."

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Using Maven..."
    mvn spring-boot:run
elif command -v ./mvnw &> /dev/null; then
    echo "Using Maven wrapper..."
    ./mvnw spring-boot:run
else
    echo "Error: Maven not found. Please install Maven or add Maven wrapper."
    echo "Install Maven: brew install maven (on macOS)"
    exit 1
fi