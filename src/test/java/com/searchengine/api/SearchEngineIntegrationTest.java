package com.searchengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "logging.level.com.searchengine=INFO")
class SearchEngineIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testIndexAndSearchFlow() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api";

        // Index some documents
        IndexController.IndexRequest doc1 = new IndexController.IndexRequest(1, "Java programming language tutorial");
        IndexController.IndexRequest doc2 = new IndexController.IndexRequest(2, "Python programming guide");
        IndexController.IndexRequest doc3 = new IndexController.IndexRequest(3, "Spring Boot framework Java");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Index document 1
        HttpEntity<IndexController.IndexRequest> request1 = new HttpEntity<>(doc1, headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(baseUrl + "/index", request1, String.class);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals("Document indexed successfully with ID: 1", response1.getBody());

        // Index document 2
        HttpEntity<IndexController.IndexRequest> request2 = new HttpEntity<>(doc2, headers);
        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl + "/index", request2, String.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Document indexed successfully with ID: 2", response2.getBody());

        // Index document 3
        HttpEntity<IndexController.IndexRequest> request3 = new HttpEntity<>(doc3, headers);
        ResponseEntity<String> response3 = restTemplate.postForEntity(baseUrl + "/index", request3, String.class);
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertEquals("Document indexed successfully with ID: 3", response3.getBody());

        // Search for "Java" - should return docs 1 and 3
        ResponseEntity<SearchController.SearchResponse> javaSearchResponse =
            restTemplate.getForEntity(baseUrl + "/search?q=Java", SearchController.SearchResponse.class);
        assertEquals(HttpStatus.OK, javaSearchResponse.getStatusCode());
        SearchController.SearchResponse javaResults = javaSearchResponse.getBody();
        assertNotNull(javaResults);
        assertEquals(2, javaResults.getTotalResults());
        assertNotNull(javaResults.getResults());
        assertEquals(2, javaResults.getResults().size());

        // Search for "programming" - should return docs 1 and 2
        ResponseEntity<SearchController.SearchResponse> programmingSearchResponse =
            restTemplate.getForEntity(baseUrl + "/search?q=programming", SearchController.SearchResponse.class);
        assertEquals(HttpStatus.OK, programmingSearchResponse.getStatusCode());
        SearchController.SearchResponse programmingResults = programmingSearchResponse.getBody();
        assertNotNull(programmingResults);
        assertEquals(2, programmingResults.getTotalResults());

        // Search for "Python" - should return doc 2
        ResponseEntity<SearchController.SearchResponse> pythonSearchResponse =
            restTemplate.getForEntity(baseUrl + "/search?q=Python", SearchController.SearchResponse.class);
        assertEquals(HttpStatus.OK, pythonSearchResponse.getStatusCode());
        SearchController.SearchResponse pythonResults = pythonSearchResponse.getBody();
        assertNotNull(pythonResults);
        assertEquals(1, pythonResults.getTotalResults());
        assertEquals(2, pythonResults.getResults().get(0).getId());

        // Search for non-existent term
        ResponseEntity<SearchController.SearchResponse> noResultsResponse =
            restTemplate.getForEntity(baseUrl + "/search?q=nonexistent", SearchController.SearchResponse.class);
        assertEquals(HttpStatus.OK, noResultsResponse.getStatusCode());
        SearchController.SearchResponse noResults = noResultsResponse.getBody();
        assertNotNull(noResults);
        assertEquals(0, noResults.getTotalResults());
    }

    @Test
    void testIndexWithInvalidData() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api";

        // Test with missing content - create minimal request with just ID
        IndexController.IndexRequest invalidRequest = new IndexController.IndexRequest(1, null);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<IndexController.IndexRequest> request = new HttpEntity<>(invalidRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/index", request, String.class);

        // Should return a bad request status
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSearchWithEmptyQuery() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api";

        ResponseEntity<SearchController.SearchResponse> response =
            restTemplate.getForEntity(baseUrl + "/search?q=", SearchController.SearchResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SearchController.SearchResponse searchResult = response.getBody();
        assertNotNull(searchResult);
        assertEquals(0, searchResult.getTotalResults());
    }
}