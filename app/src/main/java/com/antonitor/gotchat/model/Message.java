package com.antonitor.gotchat.model;

import com.google.firebase.database.ServerValue;

import java.util.Random;
import java.util.UUID;

public class Message {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private Object timeStamp;

    public Message() {
    }

    public Message(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.timeStamp = ServerValue.TIMESTAMP;
        setId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void setId() {
        this.id = UUID.randomUUID().toString();
    }
}
