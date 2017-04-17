package com.frbentes.agendaac.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frbentes on 07/04/17.
 */
@IgnoreExtraProperties
public class Appointment {

    public String uid;
    public String title;
    public String description;
    public long eventId;
    public String eventDate;

    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String uid, String title, String description, long eventId, String eventDate) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.eventId = eventId;
        this.eventDate = eventDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("description", description);
        result.put("eventId", eventId);
        result.put("eventDate", eventDate);

        return result;
    }
    
}
