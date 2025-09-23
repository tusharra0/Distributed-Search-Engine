#!/bin/bash

# Test Runner Script for Distributed Search Engine

echo "Running Distributed Search Engine Tests..."

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Using Maven to run tests..."
    mvn clean test
elif command -v ./mvnw &> /dev/null; then
    echo "Using Maven wrapper to run tests..."
    ./mvnw clean test
else
    echo "Error: Maven not found. Please install Maven or add Maven wrapper."
    echo "Install Maven: brew install maven (on macOS)"
    exit 1
fi

echo "Tests completed!"
echo ""
echo "To run the application:"
echo "  ./start.sh"
echo ""
echo "To run load tests:"
echo "  1. Start the application with ./start.sh"
echo "  2. cd load-testing"
echo "  3. python3 setup-test-data.py"
echo "  4. jmeter -t search-engine-load-test.jmx"