package com.example.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan implements Identifiable {
    private String id, memberId;
    private LocalDate date, dueDate, returnDate;
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<ReadableItem> items = new ArrayList<>();

    public Loan() {}
    public Loan(String id, String memberId, LocalDate date, LocalDate dueDate) { this.id = id; this.memberId = memberId; this.date = date; this.dueDate = dueDate; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public List<Reservation> getReservations() { return reservations; }
    public List<ReadableItem> getItems() { return items; }
    public boolean isReturned() { return returnDate != null; }
    public boolean isOverdue(LocalDate today) { return !isReturned() && dueDate != null && today.isAfter(dueDate); }
}
