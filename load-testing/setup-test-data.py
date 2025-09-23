#!/usr/bin/env python3
"""
Setup script to populate the search engine with test data before load testing.
"""

import requests
import json
import time

BASE_URL = "http://localhost:8080/api"

# Sample documents for testing
test_documents = [
    {"id": 1, "content": "Java programming tutorial for beginners"},
    {"id": 2, "content": "Spring Boot framework development guide"},
    {"id": 3, "content": "Python programming language tutorial"},
    {"id": 4, "content": "Machine learning with Python and TensorFlow"},
    {"id": 5, "content": "Web development using React and JavaScript"},
    {"id": 6, "content": "Database design and SQL programming"},
    {"id": 7, "content": "Docker containerization best practices"},
    {"id": 8, "content": "Kubernetes orchestration and deployment"},
    {"id": 9, "content": "Microservices architecture with Spring Boot"},
    {"id": 10, "content": "REST API design and implementation"},
    {"id": 11, "content": "Unit testing with JUnit and Mockito"},
    {"id": 12, "content": "Distributed systems and scalability"},
    {"id": 13, "content": "Cloud computing with AWS and Azure"},
    {"id": 14, "content": "DevOps practices and CI/CD pipelines"},
    {"id": 15, "content": "Data structures and algorithms in Java"},
    {"id": 16, "content": "Frontend development with Vue.js"},
    {"id": 17, "content": "NoSQL databases and MongoDB"},
    {"id": 18, "content": "Apache Kafka for real-time data streaming"},
    {"id": 19, "content": "Elasticsearch and full-text search"},
    {"id": 20, "content": "Performance tuning and optimization"},
]

def index_document(doc):
    """Index a single document."""
    try:
        response = requests.post(
            f"{BASE_URL}/index",
            json=doc,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        if response.status_code == 200:
            print(f"✓ Indexed document {doc['id']}: {doc['content'][:50]}...")
            return True
        else:
            print(f"✗ Failed to index document {doc['id']}: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"✗ Error indexing document {doc['id']}: {e}")
        return False

def test_search(query):
    """Test a search query."""
    try:
        response = requests.get(
            f"{BASE_URL}/search",
            params={"q": query},
            timeout=5
        )
        if response.status_code == 200:
            data = response.json()
            print(f"✓ Search '{query}' returned {data['totalResults']} results")
            return True
        else:
            print(f"✗ Search failed: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"✗ Error searching: {e}")
        return False

def wait_for_server():
    """Wait for the server to be ready."""
    print("Waiting for server to be ready...")
    for attempt in range(30):
        try:
            response = requests.get(f"{BASE_URL}/search?q=test", timeout=2)
            if response.status_code in [200, 400]:  # Server is responding
                print("✓ Server is ready!")
                return True
        except requests.exceptions.RequestException:
            pass
        time.sleep(1)

    print("✗ Server is not responding after 30 seconds")
    return False

def main():
    """Main setup function."""
    print("Setting up test data for load testing...")

    if not wait_for_server():
        return

    # Index all test documents
    success_count = 0
    for doc in test_documents:
        if index_document(doc):
            success_count += 1
        time.sleep(0.1)  # Small delay to avoid overwhelming the server

    print(f"\nIndexed {success_count}/{len(test_documents)} documents")

    # Test some searches
    print("\nTesting search functionality...")
    test_queries = ["Java", "Python", "programming", "Spring Boot", "development"]

    for query in test_queries:
        test_search(query)
        time.sleep(0.1)

    print("\n✓ Setup complete! Ready for load testing.")
    print("Run JMeter with the search-engine-load-test.jmx file")

if __name__ == "__main__":
    main()