package com.searchengine.distributed;

import com.searchengine.core.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NodeManager {
    private final ConsistentHashing hashRing = new ConsistentHashing();
    private final Map<String, InvertedIndex> shardMap = new ConcurrentHashMap<>();

    public NodeManager() {
        // bootstrap 2 nodes
        addNode("NodeA");
        addNode("NodeB");
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
        List<Map.Entry<Document, Double>> results = new ArrayList<>();
        for (InvertedIndex shard : shardMap.values()) {
            QueryProcessor qp = new QueryProcessor(shard);
            results.addAll(qp.search(query));
        }
        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results;
    }
}
