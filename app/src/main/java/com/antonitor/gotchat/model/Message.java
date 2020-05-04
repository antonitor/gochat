package com.antonitor.gotchat.model;


import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ServerValue;

import androidx.databinding.BindingAdapter;

public class Message {

    private String messageUUID;
    private String roomID;
    private String text;
    private String author;
    private String localPhotoUrl;
    private String photoUrl;
    private Object timeStamp;

    public Message() {
    }

    public Message(String messageUUID, String roomId, String author, String text, String localPhotoUrl, String photoUrl) {
        this.messageUUID = messageUUID;
        this.roomID = roomId;
        this.text = text;
        this.author = author;
        this.localPhotoUrl = localPhotoUrl;
        this.photoUrl = photoUrl;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
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

    public String getLocalPhotoUrl() {
        return localPhotoUrl;
    }

    public void setLocalPhotoUrl(String localPhotoUrl) {
        this.localPhotoUrl = localPhotoUrl;
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

    /*
    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }
     */

    @BindingAdapter("messageImage")
    public static void loadImage(ImageView view, String photoUrl) {
        Glide.with(view.getContext())
                .load(photoUrl).apply(new RequestOptions().circleCrop())
                .into(view);
    }
}
