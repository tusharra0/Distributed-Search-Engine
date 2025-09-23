package com.searchengine.core;

import java.io.Serializable;

public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String content;

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
