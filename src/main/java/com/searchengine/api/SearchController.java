package com.searchengine.api;

import com.searchengine.core.Document;
import com.searchengine.distributed.NodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private NodeManager nodeManager;

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestParam("q") String query) {
        try {
            List<Map.Entry<Document, Double>> results = nodeManager.search(query);

            List<SearchResult> searchResults = results.stream()
                .map(entry -> new SearchResult(
                    entry.getKey().getId(),
                    entry.getKey().getContent(),
                    entry.getValue()
                ))
                .collect(Collectors.toList());

            SearchResponse response = new SearchResponse(searchResults, searchResults.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new SearchResponse(List.of(), 0, "Error searching: " + e.getMessage())
            );
        }
    }

    public static class SearchResponse {
        private List<SearchResult> results;
        private int totalResults;
        private String error;

        public SearchResponse() {}

        public SearchResponse(List<SearchResult> results, int totalResults) {
            this.results = results;
            this.totalResults = totalResults;
        }

        public SearchResponse(List<SearchResult> results, int totalResults, String error) {
            this.results = results;
            this.totalResults = totalResults;
            this.error = error;
        }

        public List<SearchResult> getResults() {
            return results;
        }

        public void setResults(List<SearchResult> results) {
            this.results = results;
        }

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class SearchResult {
        private int id;
        private String content;
        private double score;

        public SearchResult() {}

        public SearchResult(int id, String content, double score) {
            this.id = id;
            this.content = content;
            this.score = score;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}