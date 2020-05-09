package com.antonitor.gotchat.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class User implements Parcelable {

    private String UUID;
    private HashMap<String, ChatRoom> ownChatRooms = new HashMap<>();
    private HashMap<String, ChatRoom> followedChatRooms = new HashMap<>();

    public User() {
    }

    public User(String uuid){
        this.UUID = uuid;
    }

    protected User(Parcel in) {
        UUID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UUID);
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public HashMap<String, ChatRoom> getOwnChatRooms() {
        return ownChatRooms;
    }

    public void setOwnChatRooms(HashMap<String, ChatRoom> ownChatRooms) {
        this.ownChatRooms = ownChatRooms;
    }

    public HashMap<String, ChatRoom> getFollowedChatRooms() {
        return followedChatRooms;
    }

    public void setFollowedChatRooms(HashMap<String, ChatRoom> followedChatRooms) {
        this.followedChatRooms = followedChatRooms;
    }
}
