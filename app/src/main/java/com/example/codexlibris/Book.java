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

    public Book() {}

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setPublished_date(String published_date) { this.published_date = published_date; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setAuthor(Author author) { this.author = author; }
    public void setGenre(Genre genre) { this.genre = genre; }



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

