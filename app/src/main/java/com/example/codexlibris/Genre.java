package com.example.codexlibris;

/**
 * Classe que representa un gènere literari dins l'aplicació.
 * Inclou informació bàsica com el nom, descripció i metadades de creació i actualització.
 */
public class Genre {
    private int id;
    private String name;
    private String description;
    private String created_at;
    private String updated_at;

    /**
     * Assigna l'identificador del gènere.
     * @param id identificador únic
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Assigna el nom del gènere.
     * @param name nom del gènere
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Assigna la descripció del gènere.
     * @param description descripció detallada
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Assigna la data de creació del gènere.
     * @param created_at data en format ISO
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Assigna la data d'actualització del gènere.
     * @param updated_at data en format ISO
     */
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * @return identificador únic del gènere
     */
    public int getId() {
        return id;
    }

    /**
     * @return nom del gènere
     */
    public String getName() {
        return name;
    }

    /**
     * @return descripció del gènere
     */
    public String getDescription() {
        return description;
    }
}
