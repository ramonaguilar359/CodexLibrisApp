package com.example.codexlibris;

public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String event_date;
    private String start_time;
    private String end_time;

    public EventRequest(String title, String description, String location, String event_date, String start_time, String end_time) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.event_date = event_date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
