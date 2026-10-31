package com.example.library.model;

import java.time.LocalDate;

public class Reservation implements Identifiable {
    public enum Status { ACTIVE, CANCELLED, COMPLETED }

    private String id, memberId, readableItemId;
    private LocalDate date;
    private Status status = Status.ACTIVE;

    public Reservation() {}
    public Reservation(String id, String memberId, String readableItemId, LocalDate date) { this.id = id; this.memberId = memberId; this.readableItemId = readableItemId; this.date = date; }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getReadableItemId() { return readableItemId; }
    public void setReadableItemId(String readableItemId) { this.readableItemId = readableItemId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean isActive() { return status == Status.ACTIVE; }
}
