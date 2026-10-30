package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public class Library implements Identifiable {
    private String id, name, address;
    private final List<Member> members = new ArrayList<>();
    private final List<ReadableItem> readableItems = new ArrayList<>();

    public Library() {}
    public Library(String id, String name, String address) { this.id = id; this.name = name; this.address = address; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Member> getMembers() { return members; }
    public List<ReadableItem> getReadableItems() { return readableItems; }
}
