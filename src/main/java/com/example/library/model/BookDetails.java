package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public class BookDetails extends Publication {
    private String isbn;
    private int pageCount;
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    public BookDetails() {
    }

    public BookDetails(String id, String title, String isbn, int pageCount) {
        super(id, title);
        this.isbn = isbn;
        this.pageCount = pageCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<BookAuthor> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(List<BookAuthor> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
}
