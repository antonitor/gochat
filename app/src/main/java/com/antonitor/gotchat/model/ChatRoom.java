package com.antonitor.gotchat.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;


public class ChatRoom {

    private String id;
    private String title;
    private String topic;
    private String owner;
    private Object timestamp;
    private String imageUrl;
    private HashMap<String, FirebaseUser> activeUsers;

    public ChatRoom() {
    }

    public ChatRoom(String id, String title, String topic, String owner, String imageUrl) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.owner = owner;
        this.timestamp = ServerValue.TIMESTAMP;
        this.imageUrl = imageUrl;
        this.activeUsers = new HashMap<String, FirebaseUser>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public HashMap<String, FirebaseUser> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(HashMap<String, FirebaseUser> activeUsers) {
        this.activeUsers = activeUsers;
    }
}