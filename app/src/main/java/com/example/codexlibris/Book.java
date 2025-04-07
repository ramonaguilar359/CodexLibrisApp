package com.example.codexlibris;

import java.time.LocalDateTime;

public class Book {
    private int id;
    private String title;
    private Author author;
    private String isbn;
    private String published_date;
    private Genre genre;
    private Boolean available;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublished_date() {
        return published_date;
    }

    public Genre getGenre() {
        return genre;
    }

    public Boolean getAvailable() {
        return available;
    }
}

