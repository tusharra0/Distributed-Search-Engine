package com.searchengine;

import com.searchengine.core.Document;
import com.searchengine.distributed.NodeManager;

import java.util.List;
import java.util.Map;

public class TestRunner {
    public static void main(String[] args) {
        NodeManager manager = new NodeManager();

        manager.addDocument(new Document(1, "Java search engine project"));
        manager.addDocument(new Document(2, "Distributed systems with Java and Spring Boot"));
        manager.addDocument(new Document(3, "Search engine using TFIDF ranking and scoring"));
        manager.addDocument(new Document(4, "Learning distributed hash tables in Java"));

        String query = "java search";
        List<Map.Entry<Document, Double>> results = manager.search(query);

        System.out.println("\n=== Search Results for: \"" + query + "\" ===");
        for (Map.Entry<Document, Double> entry : results) {
            System.out.println(
                    "Doc " + entry.getKey().getId() +
                            " | Score=" + entry.getValue() +
                            " | Content=\"" + entry.getKey().getContent() + "\""
            );
        }
    }
}
