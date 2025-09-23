package com.searchengine.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvertedIndexTest {

    private InvertedIndex index;

    @BeforeEach
    void setUp() {
        index = new InvertedIndex();
    }

    @Test
    void testAddDocumentAndGetPostings() {
        Document doc1 = new Document(1, "hello world java");
        Document doc2 = new Document(2, "hello programming java world");

        index.addDocument(doc1);
        index.addDocument(doc2);

        Map<Integer, Integer> helloPostings = index.getPostings("hello");
        assertEquals(2, helloPostings.size());
        assertEquals(1, helloPostings.get(1));
        assertEquals(1, helloPostings.get(2));

        Map<Integer, Integer> javaPostings = index.getPostings("java");
        assertEquals(2, javaPostings.size());
        assertEquals(1, javaPostings.get(1));
        assertEquals(1, javaPostings.get(2));

        Map<Integer, Integer> programmingPostings = index.getPostings("programming");
        assertEquals(1, programmingPostings.size());
        assertEquals(1, programmingPostings.get(2));
    }

    @Test
    void testGetTotalDocuments() {
        assertEquals(0, index.getTotalDocuments());

        index.addDocument(new Document(1, "test"));
        assertEquals(1, index.getTotalDocuments());

        index.addDocument(new Document(2, "another test"));
        assertEquals(2, index.getTotalDocuments());
    }

    @Test
    void testGetDocLength() {
        Document doc = new Document(1, "hello world java programming");
        index.addDocument(doc);

        assertEquals(4, index.getDocLength(1));
        assertEquals(0, index.getDocLength(999)); // non-existent doc
    }

    @Test
    void testGetDocument() {
        Document doc = new Document(1, "test content");
        index.addDocument(doc);

        Document retrieved = index.getDocument(1);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getId());
        assertEquals("test content", retrieved.getContent());

        assertNull(index.getDocument(999)); // non-existent doc
    }

    @Test
    void testCaseInsensitiveSearch() {
        Document doc = new Document(1, "Hello WORLD Java");
        index.addDocument(doc);

        Map<Integer, Integer> postings = index.getPostings("hello");
        assertEquals(1, postings.size());
        assertEquals(1, postings.get(1));

        postings = index.getPostings("WORLD");
        assertEquals(1, postings.size());
        assertEquals(1, postings.get(1));
    }
}