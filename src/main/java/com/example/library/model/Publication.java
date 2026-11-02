package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Publication implements Identifiable {
    private String id;
    private String title;
    private List<ReadableItem> copies = new ArrayList<>();

    public Publication() {
    }

    public Publication(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReadableItem> getCopies() {
        return copies;
    }

    public void setCopies(List<ReadableItem> copies) {
        this.copies = copies;
    }

    public long availableCopies() {
        long count = 0;
        for (ReadableItem item : copies) {
            if (item.isAvailable()) {
                count++;
            }
        }
        return count;
    }
}
