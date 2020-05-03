package com.antonitor.gotchat.sync;

import android.provider.ContactsContract;
import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
public class FirebaseDatabaseRepository {

    private static final String LOG_TAG = FirebaseDatabaseRepository.class.getSimpleName();
    private static FirebaseDatabaseRepository sInstance;
    private static final Object LOCK = new Object();

    private FirebaseUser firebaseUser;
    private User userChatRooms;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference chatroomsReference;
    private DatabaseReference userChatsReference;
    private DatabaseReference messageReference;

    private FirebaseDatabaseRepository(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        chatroomsReference = mFirebaseDatabase.getReference ().child(FirebaseContract.CHATROOMS_REF);
        userChatsReference = mFirebaseDatabase.getReference().child(FirebaseContract.USER_REF);
        messageReference = mFirebaseDatabase.getReference().child(FirebaseContract.MESSAGES_REF);
    }

    public static FirebaseDatabaseRepository getInstance() {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseDatabaseRepository();
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    /**
     * Add new Chat Room stored as /chatrooms/roomID
     * @param roomID chatroom will be stored under this child
     * @param title room title
     * @param topic room topic
     * @param photoUrl room photourl
     */
    public void newChatRoom(String roomID, String title, String topic, String photoUrl){
        ChatRoom room = new ChatRoom(roomID, title, topic, firebaseUser.getUid(), photoUrl);
        chatroomsReference.child(roomID).setValue(room);
        addOwnChatRoom(room);
    }

    /**
     * Add this chatroom to the OWN CHATROOMS LIST
     * Stored as /userchats/userID/ownChatRooms/roomID
     * @param room objec stored
     */
    private void addOwnChatRoom(ChatRoom room) {
        String userUid = firebaseUser.getUid();
        userChatRooms.getOwnChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.OWN_ROOMS_REF).setValue(userChatRooms.getOwnChatRooms());
    }

    public void addFollowingChat(ChatRoom room) {
        String userUid = firebaseUser.getUid();
        userChatRooms.getFollowedChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).setValue(userChatRooms.getFollowedChatRooms());
    }

    public void unFollowChat(ChatRoom room) {
        String userUid = firebaseUser.getUid();
        userChatRooms.getFollowedChatRooms().remove(room.getId());
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(room.getId()).removeValue();
    }

    public void removeChatRoom(String roomID) {
        String userUid = firebaseUser.getUid();
        chatroomsReference.child(roomID).removeValue((databaseError, databaseReference) -> {
            userChatRooms.getOwnChatRooms().remove(roomID);
            userChatRooms.getFollowedChatRooms().remove(roomID);
            userChatsReference.child(userUid).child(FirebaseContract.OWN_ROOMS_REF).child(roomID).removeValue();
            userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(roomID).removeValue();
            messageReference.child(roomID).removeValue();
        });
    }

    public Message postMessage(Message message) {
        String key = messageReference.child(message.getRoomID()).push().getKey();
        messageReference.child(message.getRoomID()).child(key).setValue(message);
        message.setMessageUUID(key);
        return message;
    }

    public void setImageMessage(Message message) {
        messageReference.child(message.getRoomID()).child(message.getMessageUUID()).setValue(message);
    }

    private Query getTrendingRoomsQuery(){
        return mFirebaseDatabase.getReference().child(FirebaseContract.CHATROOMS_REF);
    }

    private Query getFollowingRoomsQuery(){
        return userChatsReference.child(firebaseUser.getUid()).child(FirebaseContract.FOLLOWED_ROOMS_REF);
    }

    private Query getOwnChatRoomsQuery(){
        return userChatsReference.child(firebaseUser.getUid()).child(FirebaseContract.OWN_ROOMS_REF);
    }

    private Query getMessageListQuery(String roomId){
        return messageReference.child(roomId);
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



    public FirebaseRecyclerOptions<Message> getMessageListOptions(String roomID){
        return new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(getMessageListQuery(roomID), Message.class)
                .build();
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser user) {
        this.firebaseUser = user;
        setUpUserChatRooms();
    }

    private void setUpUserChatRooms() {
        userChatsReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userChatRooms = dataSnapshot.getValue(User.class);
                if (userChatRooms != null) {
                    if (userChatRooms.getOwnChatRooms() == null)
                        userChatRooms.setOwnChatRooms(new HashMap<>());
                    if (userChatRooms.getFollowedChatRooms() == null)
                        userChatRooms.setFollowedChatRooms(new HashMap<>());
                } else {
                    userChatRooms = new User(new HashMap<>(), new HashMap<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, databaseError.getMessage());
            }
        });
    }
}
