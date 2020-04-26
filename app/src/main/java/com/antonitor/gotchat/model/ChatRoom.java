package com.antonitor.gotchat.model;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ChatRoom {

    private String title;
    private String topic;
    private String owner;
    private Object timestamp;
    private String imageUrl;

    public ChatRoom() {
    }

    public ChatRoom(String title, String topic, String owner, String imageUrl) {
        this.title = title;
        this.topic = topic;
        this.owner = owner;
        this.timestamp = ServerValue.TIMESTAMP;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}