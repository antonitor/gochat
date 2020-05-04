package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
public class FirebaseDatabaseRepository {

    private static final String LOG_TAG = FirebaseDatabaseRepository.class.getSimpleName();
    private static FirebaseDatabaseRepository sInstance;
    private static final Object LOCK = new Object();

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
        ChatRoom room = new ChatRoom(
                roomID,
                title,
                topic,
                FirebaseAuthRepository.getInstance().getFirebaseUser().getUid(),
                photoUrl);
        chatroomsReference.child(roomID).setValue(room);
        addOwnChatRoom(room);
    }

    /**
     * Add this chatroom to the OWN CHATROOMS LIST
     * Stored as /userchats/userID/ownChatRooms/roomID
     * @param room objec stored
     */
    private void addOwnChatRoom(ChatRoom room) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        FirebaseAuthRepository.getInstance().getUser().getOwnChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.OWN_ROOMS_REF).setValue(FirebaseAuthRepository.getInstance().getUser().getOwnChatRooms());
    }

    public void addFollowingChat(ChatRoom room) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        FirebaseAuthRepository.getInstance().getUser().getFollowedChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).setValue(FirebaseAuthRepository.getInstance().getUser().getFollowedChatRooms());
    }

    public void unFollowChat(ChatRoom room) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        FirebaseAuthRepository.getInstance().getUser().getFollowedChatRooms().remove(room.getId());
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(room.getId()).removeValue();
    }

    public void removeChatRoom(String roomID) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        chatroomsReference.child(roomID).removeValue((databaseError, databaseReference) -> {
            FirebaseAuthRepository.getInstance().getUser().getOwnChatRooms().remove(roomID);
            FirebaseAuthRepository.getInstance().getUser().getFollowedChatRooms().remove(roomID);
            userChatsReference.child(userUid).child(FirebaseContract.OWN_ROOMS_REF).child(roomID).removeValue();
            userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(roomID).removeValue();
            messageReference.child(roomID).removeValue();
        });
    }

    public Message postMessage(Message message) {
        messageReference.child(message.getRoomID()).push().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                message.setMessageUUID(key);
                messageReference.child(message.getRoomID()).child(key).setValue(message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                
            }
        });
        return message;
    }

    public void updateMessage(Message message) {
        messageReference.child(message.getRoomID()).child(message.getMessageUUID()).setValue(message);
    }

    private Query getTrendingRoomsQuery(){
        return mFirebaseDatabase.getReference().child(FirebaseContract.CHATROOMS_REF);
    }

    private Query getFollowingRoomsQuery(){
        return userChatsReference.child(FirebaseAuthRepository.getInstance().getFirebaseUser().getUid()).child(FirebaseContract.FOLLOWED_ROOMS_REF);
    }

    private Query getOwnChatRoomsQuery(){
        return userChatsReference.child(FirebaseAuthRepository.getInstance().getFirebaseUser().getUid()).child(FirebaseContract.OWN_ROOMS_REF);
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

    public DatabaseReference getUserChatsReference() {
        return userChatsReference;
    }
}
