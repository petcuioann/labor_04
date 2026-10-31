package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Publication implements Identifiable {
    private String id, title;
    private final List<ReadableItem> copies = new ArrayList<>();

    protected Publication() {}
    protected Publication(String id, String title) { this.id = id; this.title = title; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<ReadableItem> getCopies() { return copies; }
    public long availableCopies() { return copies.stream().filter(ReadableItem::isAvailable).count(); }
}
