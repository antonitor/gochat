package com.antonitor.gotchat.model;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ServerValue;

import androidx.databinding.BindingAdapter;

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

    @BindingAdapter("chatImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }
}
