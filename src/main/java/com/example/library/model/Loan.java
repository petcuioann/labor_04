package com.example.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Loan implements Identifiable {
    private String id;
    private String memberId;
    private LocalDate date;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private List<Reservation> reservations = new ArrayList<>();
    private List<ReadableItem> items = new ArrayList<>();

    public Loan() {
    }

    public Loan(String id, String memberId, LocalDate date, LocalDate dueDate) {
        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.dueDate = dueDate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public List<ReadableItem> getItems() {
        return items;
    }

    public void setItems(List<ReadableItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @JsonIgnore
    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        if (isReturned() || dueDate == null) {
            return false;
        }
        return today.isAfter(dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
