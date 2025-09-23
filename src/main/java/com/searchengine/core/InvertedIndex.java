package com.searchengine.core;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex implements Serializable {
    private static final long serialVersionUID = 1L;
    // term -> (docId -> frequency)
    private final Map<String, Map<Integer, Integer>> index = new ConcurrentHashMap<>();
    private final Map<Integer, Document> documents = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> docLengths = new ConcurrentHashMap<>();

    public void addDocument(Document doc) {
        documents.put(doc.getId(), doc);

        String[] tokens = doc.getContent().toLowerCase().split("\\s+");
        docLengths.put(doc.getId(), tokens.length);

        for (String token : tokens) {
            index.putIfAbsent(token, new ConcurrentHashMap<>());
            Map<Integer, Integer> postings = index.get(token);
            postings.put(doc.getId(), postings.getOrDefault(doc.getId(), 0) + 1);
        }
    }

    public Map<Integer, Integer> getPostings(String term) {
        return index.getOrDefault(term.toLowerCase(), Collections.emptyMap());
    }

    public int getTotalDocuments() {
        return documents.size();
    }

    public int getDocLength(int docId) {
        return docLengths.getOrDefault(docId, 0);
    }

    public Document getDocument(int docId) {
        return documents.get(docId);
    }
}
