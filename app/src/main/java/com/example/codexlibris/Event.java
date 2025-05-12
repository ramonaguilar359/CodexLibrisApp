package com.example.codexlibris;

/**
 * Classe que representa un esdeveniment.
 */
public class Event {
    private int id;
    private String title;
    private String description;
    private String location;
    private String event_date;
    private String start_time;
    private String end_time;

    /**
     * Retorna l'ID de l'esdeveniment.
     */
    public int getId() {
        return id;
    }

    /**
     * Assigna l'ID de l'esdeveniment.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retorna el títol de l'esdeveniment.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Assigna el títol de l'esdeveniment.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retorna la descripció de l'esdeveniment.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigna la descripció de l'esdeveniment.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retorna la ubicació on tindrà lloc l'esdeveniment.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Assigna la ubicació de l'esdeveniment.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Retorna la data de l'esdeveniment en format ISO (yyyy-MM-dd).
     */
    public String getEvent_date() {
        return event_date;
    }

    /**
     * Assigna la data de l'esdeveniment en format ISO (yyyy-MM-dd).
     */
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    /**
     * Retorna l'hora d'inici de l'esdeveniment (format HH:mm:ss).
     */
    public String getStart_time() {
        return start_time;
    }

    /**
     * Assigna l'hora d'inici de l'esdeveniment (format HH:mm:ss).
     */
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    /**
     * Retorna l'hora de finalització de l'esdeveniment (format HH:mm:ss).
     */
    public String getEnd_time() {
        return end_time;
    }

    /**
     * Assigna l'hora de finalització de l'esdeveniment (format HH:mm:ss).
     */
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    /**
     * Constructor complet per inicialitzar totes les propietats d’un esdeveniment.
     *
     * @param id ID de l'esdeveniment
     * @param title títol
     * @param description descripció
     * @param location ubicació
     * @param event_date data (format yyyy-MM-dd)
     * @param start_time hora d'inici (format HH:mm:ss)
     * @param end_time hora de finalització (format HH:mm:ss)
     */
    public Event(int id, String title, String description, String location, String event_date, String start_time, String end_time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.event_date = event_date;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}
