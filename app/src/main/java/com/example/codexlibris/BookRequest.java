package com.example.codexlibris;

public class BookRequest {
    private String title;
    private int authorId;
    private String isbn;
    private String publishedDate;
    private int genreId;
    private boolean available;

    public BookRequest(String title, int authorId, String isbn, String publishedDate, int genreId, boolean available) {
        this.title = title;
        this.authorId = authorId;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.genreId = genreId;
        this.available = available;
    }
}

