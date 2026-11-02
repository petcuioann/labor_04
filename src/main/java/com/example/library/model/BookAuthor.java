package com.example.library.model;

public class BookAuthor implements Identifiable {
    private String id;
    private String bookId;
    private String authorId;

    public BookAuthor() {
    }

    public BookAuthor(String id, String bookId, String authorId) {
        this.id = id;
        this.bookId = bookId;
        this.authorId = authorId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
