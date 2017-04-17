package com.frbentes.agendaac.model.dto;

/**
 * Created by frbentes on 17/04/17.
 */
public class AppointmentDTO {
    public String key;
    public String title;
    public String description;
    public String eventDate;

    public AppointmentDTO(String key, String title, String description, String eventDate) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
    }
}
