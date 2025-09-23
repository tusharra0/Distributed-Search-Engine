package com.searchengine.api;

import com.searchengine.core.Document;
import com.searchengine.distributed.NodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IndexController {

    @Autowired
    private NodeManager nodeManager;

    @PostMapping("/index")
    public ResponseEntity<String> indexDocument(@RequestBody IndexRequest request) {
        try {
            // Validate request
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Document content cannot be null or empty");
            }

            Document doc = new Document(request.getId(), request.getContent());
            nodeManager.addDocument(doc);
            return ResponseEntity.ok("Document indexed successfully with ID: " + request.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error indexing document: " + e.getMessage());
        }
    }

    public static class IndexRequest {
        private int id;
        private String content;

        public IndexRequest() {}

        public IndexRequest(int id, String content) {
            this.id = id;
            this.content = content;
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
    }
}