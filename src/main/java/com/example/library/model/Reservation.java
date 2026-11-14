package com.example.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reservation implements Identifiable {
    public enum Status {
        ACTIVE, CANCELLED, COMPLETED
    }

    private String id;
    private String memberId;
    private String readableItemId;
    private LocalDate date;
    private Status status = Status.ACTIVE;

    public Reservation() {
    }

    public Reservation(String id, String memberId, String readableItemId, LocalDate date) {
        this.id = id;
        this.memberId = memberId;
        this.readableItemId = readableItemId;
        this.date = date;
        this.status = Status.ACTIVE;
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

    public String getReadableItemId() {
        return readableItemId;
    }

    public void setReadableItemId(String readableItemId) {
        this.readableItemId = readableItemId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status != null ? status : Status.ACTIVE;
    }

    @JsonIgnore
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
