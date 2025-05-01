package com.example.codexlibris;

public class AuthorRequest {
    private String name;
    private String birthDate;
    private String nationality;

    public AuthorRequest(String name, String birthDate, String nationality) {
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    // Getters (opcionals si només s'utilitza per enviar)
    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    // Setters (opcionals, segons necessitats)
    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
