# Load Testing Setup

This directory contains JMeter configuration and scripts for load testing the Distributed Search Engine.

## Prerequisites

1. **JMeter**: Download and install Apache JMeter from https://jmeter.apache.org/
2. **Python 3**: For running the test data setup script
3. **Running Application**: Ensure the search engine is running on `localhost:8080`

## Setup

1. Start the search engine application:
   ```bash
   cd /Applications/Development/Distributed-Search-Engine/Distributed-Search-Engine
   mvn spring-boot:run
   ```

2. Set up test data:
   ```bash
   cd load-testing
   python3 setup-test-data.py
   ```

## Running Load Tests

### JMeter GUI Mode (for development)
```bash
jmeter -t search-engine-load-test.jmx
```

### JMeter Command Line Mode (for automation)
```bash
jmeter -n -t search-engine-load-test.jmx -l results.jtl -e -o report-output/
```

## Test Configuration

The default JMeter test plan includes:
- **50 concurrent users** (threads)
- **10-second ramp-up time**
- **10 iterations** per user
- **Search queries** for "java programming"

### Customizing the Test

Edit `search-engine-load-test.jmx` to modify:
- Number of threads (concurrent users)
- Ramp-up time
- Number of iterations
- Search queries
- Target server and port

## Performance Metrics

Key metrics to monitor:
- **Response Time**: Average, 95th percentile, maximum
- **Throughput**: Requests per second
- **Error Rate**: Percentage of failed requests
- **CPU and Memory Usage**: Server resource utilization

## Expected Performance

Based on the current implementation with:
- 2 shards (NodeA, NodeB)
- 8-thread ExecutorService for parallel search
- In-memory inverted index

Estimated performance targets:
- **Throughput**: 500-1000 requests/second
- **Response Time**: < 100ms for 95th percentile
- **Error Rate**: < 1%

## Scaling Test Scenarios

### Scenario 1: Baseline (Current Configuration)
- 50 users, 10-second ramp-up, 10 iterations

### Scenario 2: High Load
- 200 users, 30-second ramp-up, 20 iterations

### Scenario 3: Stress Test
- 500 users, 60-second ramp-up, 5 iterations

### Scenario 4: Endurance Test
- 100 users, constant load for 10 minutes

## Monitoring

Monitor the following during load tests:
- JVM heap usage
- GC frequency and duration
- Thread pool utilization
- System CPU and memory
- Network I/O

## Troubleshooting

Common issues and solutions:

1. **Connection refused**: Ensure the application is running on port 8080
2. **High response times**: Check CPU utilization and consider increasing thread pool size
3. **Out of memory**: Increase JVM heap size with `-Xmx2g` parameter
4. **Test data missing**: Run `setup-test-data.py` before load testing

## Advanced Testing

For more comprehensive testing, consider:
- Multiple query patterns (single word, phrase, boolean)
- Mixed workload (indexing + searching)
- Different document sizes
- Persistence layer performance impact
- Network latency simulation