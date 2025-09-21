package com.searchengine.core;

import java.util.Map;

public class TFIDFCalculator {
    private final InvertedIndex index;

    public TFIDFCalculator(InvertedIndex index) {
        this.index = index;
    }

    public double tf(String term, int docId) {
        Map<Integer, Integer> postings = index.getPostings(term);
        int freq = postings.getOrDefault(docId, 0);
        int length = index.getDocLength(docId);
        return length == 0 ? 0.0 : (double) freq / length;
    }

    public double idf(String term) {
        int N = index.getTotalDocuments();
        int df = index.getPostings(term).size();
        return Math.log((N + 1.0) / (1 + df)) + 1.0;
    }

    public double tfidf(String term, int docId) {
        return tf(term, docId) * idf(term);
    }
}
