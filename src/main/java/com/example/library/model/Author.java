package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public class Author implements Identifiable {
    private String id, name;
    private final List<BookAuthor> books = new ArrayList<>();

    public Author() {}
    public Author(String id, String name) { this.id = id; this.name = name; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<BookAuthor> getBooks() { return books; }
}
