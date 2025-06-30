package org.unisalento.iotproject.domain;

import org.springframework.data.annotation.Id;

public class Message {

    @Id
    private String id;
    private String message;
    private String date;
    private String topic;
    private String mittenteId;
    private String destinatarioId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMittenteId() {
        return mittenteId;
    }

    public void setMittenteId(String mittenteId) {
        this.mittenteId = mittenteId;
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
