package com.example.codexlibris;

/**
 * Representa un autor registrat a l'aplicació.
 */
public class Author {
    private int id;
    private String name;
    private String birth_date;
    private String nationality;
    private String created_at;
    private String updated_at;

    /**
     * Estableix l'identificador de l'autor.
     * @param id identificador únic
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Estableix el nom de l'autor.
     * @param name nom complet
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Estableix la data de naixement de l'autor.
     * @param birth_date data de naixement en format ISO (YYYY-MM-DD)
     */
    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    /**
     * Estableix la nacionalitat de l'autor.
     * @param nationality país d'origen o nacionalitat
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /**
     * Estableix la data de creació del registre.
     * @param created_at data i hora de creació
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Estableix la data d'actualització del registre.
     * @param updated_at data i hora de l'última modificació
     */
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Retorna l'identificador de l'autor.
     * @return identificador únic
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el nom de l'autor.
     * @return nom complet
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna la data de naixement de l'autor.
     * @return data de naixement
     */
    public String getBirth_date() {
        return birth_date;
    }

    /**
     * Retorna la nacionalitat de l'autor.
     * @return nacionalitat
     */
    public String getNationality() {
        return nationality;
    }
}

