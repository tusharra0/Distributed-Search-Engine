package com.searchengine.core;

import java.util.*;

public class QueryProcessor {
    private final InvertedIndex index;
    private final TFIDFCalculator tfidfCalculator;

    public QueryProcessor(InvertedIndex index) {
        this.index = index;
        this.tfidfCalculator = new TFIDFCalculator(index);
    }

    public List<Map.Entry<Document, Double>> search(String query) {
        String[] terms = query.toLowerCase().split("\\s+");
        Map<Integer, Double> scores = new HashMap<>();

        for (String term : terms) {
            Map<Integer, Integer> postings = index.getPostings(term);
            for (Integer docId : postings.keySet()) {
                double score = tfidfCalculator.tfidf(term, docId);
                scores.put(docId, scores.getOrDefault(docId, 0.0) + score);
            }
        }

        // rank results
        List<Map.Entry<Document, Double>> results = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
            results.add(Map.entry(index.getDocument(entry.getKey()), entry.getValue()));
        }

        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results;
    }
}
