package com.example.library.model;

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
        this.status = status;
    }

    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }
}
