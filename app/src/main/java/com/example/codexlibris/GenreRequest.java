package com.example.codexlibris;

/**
 * Classe utilitzada per enviar les dades d’un nou gènere o d’un gènere actualitzat al servidor.
 *
 * @property name Nom del gènere.
 * @property description Descripció del gènere.
 */
public class GenreRequest {
    private String name;
    private String description;

    /**
     * Constructor que inicialitza un nou gènere amb el nom i la descripció proporcionats.
     *
     * @param name Nom del gènere.
     * @param description Descripció del gènere.
     */
    public GenreRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
