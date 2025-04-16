package com.example.codexlibris;

public class LibroReservado {
    private String title;
    private String author;

    public LibroReservado(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}

