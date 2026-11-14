package com.example.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadableItem implements Identifiable {
    public enum Status {
        AVAILABLE, BORROWED, RESERVED
    }

    private String id;
    private String publicationId;
    private String barcode;
    private Status status = Status.AVAILABLE;

    public ReadableItem() {
    }

    public ReadableItem(String id, String publicationId, String barcode) {
        this.id = id;
        this.publicationId = publicationId;
        this.barcode = barcode;
        this.status = Status.AVAILABLE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status != null ? status : Status.AVAILABLE;
    }

    @JsonIgnore
    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReadableItem that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
