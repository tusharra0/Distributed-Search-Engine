package com.searchengine.distributed;

/**
 * Placeholder for real cluster communication (like gRPC/REST between nodes).
 * For now, it just simulates a heartbeat mechanism.
 */
public class ClusterCommunicator {

    public void sendHeartbeat(String fromNode, String toNode) {
        System.out.println("Heartbeat from " + fromNode + " -> " + toNode);
    }

    public void replicateData(String fromNode, String toNode, Object data) {
        System.out.println("Replicating data from " + fromNode + " -> " + toNode);
    }
}
