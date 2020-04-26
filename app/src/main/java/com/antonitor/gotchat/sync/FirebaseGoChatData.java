package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseGoChatData {

    private static final String LOG_TAG = FirebaseGoChatData.class.getSimpleName();
    private static FirebaseGoChatData sInstance;
    private static final Object LOCK = new Object();

    private static final String CHATROOMS_REF = "chatrooms";
    private static final String MESSAGES_REF = "messages";

    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference chatroomsReference;

    private FirebaseGoChatData(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        chatroomsReference = mFirebaseDatabase.getReference().child(CHATROOMS_REF);
    }



    public static FirebaseGoChatData getInstance() {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseGoChatData();
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void newChatRoom(String roomID, String title, String topic, String photoUrl){
        ChatRoom room = new ChatRoom(title, topic, firebaseUser.getPhoneNumber(), photoUrl);
        chatroomsReference.child(roomID).setValue(room);
    }



    public Query getTrendingRoomsQuery(){
        //return mFirebaseDatabase.getReference().child("/chatrooms").orderByChild("likes_count");
        return mFirebaseDatabase.getReference().child("chatrooms");
    }

    public Query getFollowingRoomsQuery(){
        //return mFirebaseDatabase.getReference().child("/chatrooms").orderByChild("favorites/"+getFirebaseUser().getPhoneNumber()).equalTo(true);
        return mFirebaseDatabase.getReference().child("chatrooms");
    }

    public Query getOwnChatRoomsQuery(){
        //return mFirebaseDatabase.getReference().child("/chatrooms").child("owner").equalTo(getFirebaseUser().getPhoneNumber());
        return mFirebaseDatabase.getReference().child("chatrooms");
    }

    public FirebaseRecyclerOptions<ChatRoom> getTrendingChatRoomListOptions(){
        return new FirebaseRecyclerOptions.Builder<ChatRoom>()
                .setQuery(getTrendingRoomsQuery(), ChatRoom.class)
                .build();
    }

    public FirebaseRecyclerOptions<ChatRoom> getFollowedChatRoomListOptions(){
        return new FirebaseRecyclerOptions.Builder<ChatRoom>()
                .setQuery(getFollowingRoomsQuery(), ChatRoom.class)
                .build();
    }

    public FirebaseRecyclerOptions<ChatRoom> getOwnChatRoomListOptions(){
        return new FirebaseRecyclerOptions.Builder<ChatRoom>()
                .setQuery(getOwnChatRoomsQuery(), ChatRoom.class)
                .build();
    }

    public FirebaseDatabase getmFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser user) {
        this.firebaseUser = user;
    }
}
