package com.example.library.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = BookDetails.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BookDetails.class, name = "BOOK"),
    @JsonSubTypes.Type(value = MagazineDetails.class, name = "MAGAZINE")
})
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
        this.copies = copies != null ? copies : new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publication that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
