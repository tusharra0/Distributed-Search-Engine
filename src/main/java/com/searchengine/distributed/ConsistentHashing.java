package com.searchengine.distributed;

import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {
    private final SortedMap<Integer, String> ring = new TreeMap<>();

    public void addNode(String nodeId) {
        ring.put(nodeId.hashCode(), nodeId);
    }

    public void removeNode(String nodeId) {
        ring.remove(nodeId.hashCode());
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hash = key.hashCode();
        if (!ring.containsKey(hash)) {
            SortedMap<Integer, String> tail = ring.tailMap(hash);
            hash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        }
        return ring.get(hash);
    }
}
