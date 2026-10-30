package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public class Member implements Identifiable {
    private String id, name, email, libraryId;
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public Member() {}
    public Member(String id, String name, String email, String libraryId) { this.id = id; this.name = name; this.email = email; this.libraryId = libraryId; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }
    public List<Reservation> getReservations() { return reservations; }
    public List<Loan> getLoans() { return loans; }
    public boolean hasOpenLoans() { return loans.stream().anyMatch(l -> !l.isReturned()); }
}
