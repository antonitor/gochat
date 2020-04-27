package com.antonitor.gotchat.model;

import java.util.HashMap;

public class User {

    private HashMap<String, ChatRoom> ownChatRooms;
    private HashMap<String, ChatRoom> followedChatRooms;

    public User() {
    }

    public User(HashMap<String, ChatRoom> ownChatRooms, HashMap<String, ChatRoom> followedChatRooms) {
            if (ownChatRooms != null)
                this.ownChatRooms = ownChatRooms;
            if (followedChatRooms != null)
                this.followedChatRooms = followedChatRooms;
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
