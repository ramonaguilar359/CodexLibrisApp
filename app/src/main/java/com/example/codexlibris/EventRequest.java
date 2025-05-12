package com.example.codexlibris;

/**
 * Classe utilitzada per crear o actualitzar esdeveniments mitjançant peticions a l’API.
 */
public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String event_date;
    private String start_time;
    private String end_time;

    /**
     * Constructor per inicialitzar una petició d’esdeveniment.
     *
     * @param title        títol de l’esdeveniment
     * @param description  descripció de l’esdeveniment
     * @param location     lloc de celebració
     * @param event_date   data de l’esdeveniment (format ISO: yyyy-MM-dd)
     * @param start_time   hora d’inici (format HH:mm:ss)
     * @param end_time     hora de finalització (format HH:mm:ss)
     */
    public EventRequest(String title, String description, String location, String event_date, String start_time, String end_time) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.event_date = event_date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    /**
     * @return títol de l’esdeveniment
     */
    public String getTitle() {
        return title;
    }

    /**
     * Assigna el títol de l’esdeveniment.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return descripció de l’esdeveniment
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigna la descripció de l’esdeveniment.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return ubicació de l’esdeveniment
     */
    public String getLocation() {
        return location;
    }

    /**
     * Assigna la ubicació de l’esdeveniment.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return data de l’esdeveniment (format ISO)
     */
    public String getEvent_date() {
        return event_date;
    }

    /**
     * Assigna la data de l’esdeveniment (format ISO).
     */
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    /**
     * @return hora d’inici de l’esdeveniment
     */
    public String getStart_time() {
        return start_time;
    }

    /**
     * Assigna l’hora d’inici de l’esdeveniment.
     */
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    /**
     * @return hora de finalització de l’esdeveniment
     */
    public String getEnd_time() {
        return end_time;
    }

    /**
     * Assigna l’hora de finalització de l’esdeveniment.
     */
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
