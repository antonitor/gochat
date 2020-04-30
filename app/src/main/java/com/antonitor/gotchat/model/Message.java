package com.antonitor.gotchat.model;

import com.google.firebase.database.ServerValue;

public class Message {

    private String roomID;
    private String text;
    private String author;
    private String photoUrl;
    private Object timeStamp;

    public Message() {
    }

    public Message(String roomId, String text, String author, String photoUrl) {
        this.roomID = roomId;
        this.text = text;
        this.author = author;
        this.photoUrl = photoUrl;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
