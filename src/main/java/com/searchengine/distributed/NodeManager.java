package com.searchengine.distributed;

import com.searchengine.core.*;
import com.searchengine.persistence.PersistenceService;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class NodeManager {
    private final ConsistentHashing hashRing = new ConsistentHashing();
    private final Map<String, InvertedIndex> shardMap = new ConcurrentHashMap<>();
    private final ExecutorService searchExecutor = Executors.newFixedThreadPool(8);
    private final PersistenceService persistenceService;

    public NodeManager(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        loadFromPersistence();
    }

    // Convenience constructor for testing (creates its own PersistenceService)
    public NodeManager() {
        this(new PersistenceService());
    }

    private void loadFromPersistence() {
        Map<String, InvertedIndex> persistedShards = persistenceService.loadShardMap();

        if (persistedShards.isEmpty()) {
            // bootstrap 2 nodes if no persisted data
            addNode("NodeA");
            addNode("NodeB");
        } else {
            // restore from persistence
            for (String nodeId : persistedShards.keySet()) {
                hashRing.addNode(nodeId);
                shardMap.put(nodeId, persistedShards.get(nodeId));
            }
        }
    }

    public void addNode(String nodeId) {
        hashRing.addNode(nodeId);
        shardMap.put(nodeId, new InvertedIndex());
    }

    public void removeNode(String nodeId) {
        hashRing.removeNode(nodeId);
        shardMap.remove(nodeId);
    }

    public void addDocument(Document doc) {
        String node = hashRing.getNode(String.valueOf(doc.getId()));
        if (node != null) {
            shardMap.get(node).addDocument(doc);
        }
    }

    public List<Map.Entry<Document, Double>> search(String query) {
        List<CompletableFuture<List<Map.Entry<Document, Double>>>> futures =
            shardMap.values().stream()
                .map(shard -> CompletableFuture.supplyAsync(() -> {
                    QueryProcessor qp = new QueryProcessor(shard);
                    return qp.search(query);
                }, searchExecutor))
                .collect(Collectors.toList());

        List<Map.Entry<Document, Double>> results = futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results;
    }

    public void saveToPersistence() {
        persistenceService.saveShardMap(shardMap);
    }

    public void shutdown() {
        saveToPersistence();
        searchExecutor.shutdown();
        try {
            if (!searchExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                searchExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            searchExecutor.shutdownNow();
        }
    }
}
