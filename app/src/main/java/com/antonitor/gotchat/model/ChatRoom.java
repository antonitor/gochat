package com.antonitor.gotchat.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import androidx.databinding.BindingAdapter;


public class ChatRoom implements Parcelable {

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
        this.activeUsers = new HashMap<>();
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

    protected ChatRoom(Parcel in) {
        id = in.readString();
        title = in.readString();
        topic = in.readString();
        owner = in.readString();
        imageUrl = in.readString();
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

    @BindingAdapter("roomImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl).apply(new RequestOptions().circleCrop())
                .into(view);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(topic);
        parcel.writeString(owner);
        parcel.writeString(imageUrl);
    }
}