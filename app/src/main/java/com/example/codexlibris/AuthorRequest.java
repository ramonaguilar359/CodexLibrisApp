package com.example.codexlibris;

/**
 * Representa la petició per crear o actualitzar un autor.
 */
public class AuthorRequest {
    private String name;
    private String birthDate;
    private String nationality;

    /**
     * Constructor per inicialitzar un objecte AuthorRequest.
     *
     * @param name nom complet de l'autor
     * @param birthDate data de naixement (format ISO: YYYY-MM-DD)
     * @param nationality nacionalitat de l'autor
     */
    public AuthorRequest(String name, String birthDate, String nationality) {
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    // Getters

    /**
     * Retorna el nom de l'autor.
     * @return nom de l'autor
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna la data de naixement de l'autor.
     * @return data de naixement
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Retorna la nacionalitat de l'autor.
     * @return nacionalitat
     */
    public String getNationality() {
        return nationality;
    }

    // Setters

    /**
     * Estableix el nom de l'autor.
     * @param name nou nom
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Estableix la data de naixement de l'autor.
     * @param birthDate nova data de naixement
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Estableix la nacionalitat de l'autor.
     * @param nationality nova nacionalitat
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
