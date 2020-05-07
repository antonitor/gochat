package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FirebaseDatabaseRepository {

    private static final String LOG_TAG = FirebaseDatabaseRepository.class.getSimpleName();
    private static FirebaseDatabaseRepository sInstance;
    private static final Object LOCK = new Object();

    private final FirebaseDatabase mFirebaseDatabase;
    private final DatabaseReference chatroomsReference;
    private final DatabaseReference userChatsReference;
    private final DatabaseReference messageReference;
    private FirebaseDatabaseRepositoryCallback<Message> messagesCallback;
    private ValueEventListener messagesListener;
    private FirebaseDatabaseRepositoryCallback<ChatRoom> roomsCallback;
    private ValueEventListener roomsListener;

    public interface FirebaseDatabaseRepositoryCallback<T> {
        void onSuccess(List<T> result);
        void onError(Exception e);
    }

    private FirebaseDatabaseRepository(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        chatroomsReference = mFirebaseDatabase.getReference ().child(FirebaseContract.CHAT_ROOMS_REF);
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


    public void addMessagesListener(String roomID, FirebaseDatabaseRepositoryCallback<Message> callback) {
        this.messagesCallback = callback;
        messagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    messageList.add(item.getValue(Message.class));
                }
                messagesCallback.onSuccess(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                messagesCallback.onError(new Exception(databaseError.getMessage()));
            }
        };
        messageReference.child(roomID).addValueEventListener(messagesListener);
    }

    public void removeMessageListener() {
        if (messageReference!=null)
            messageReference.removeEventListener(messagesListener);
    }


    public void addRoomsListener(FirebaseDatabaseRepositoryCallback<ChatRoom> callback) {
        this.roomsCallback = callback;
        roomsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatRoom> roomsList = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    roomsList.add(item.getValue(ChatRoom.class));
                }
                roomsCallback.onSuccess(roomsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                roomsCallback.onError(new Exception(databaseError.getMessage()));
            }
        };
        chatroomsReference.addValueEventListener(roomsListener);
    }

    public void removeRoomsListener() {
        if (roomsListener!=null)
            chatroomsReference.removeEventListener(roomsListener);
    }


    /**
     * Add new Chat Room stored as /chatrooms/roomID
     * @param roomID chatroom will be stored under this child
     * @param title room title
     * @param topic room topic
     * @param photoUrl room photourl
     */
    public void newChatRoom(String roomID, String title, String topic, String photoUrl, User owner){
        ChatRoom room = new ChatRoom(
                roomID,
                title,
                topic,
                FirebaseAuthRepository.getInstance().getFirebaseUser().getUid(),
                photoUrl);
        chatroomsReference.child(roomID).setValue(room);
        addOwnChatRoom(room, owner);
    }

    /**
     * Add this chatroom to the OWN CHAT ROOMS LIST
     * Stored as /userchats/userID/ownChatRooms/roomID
     * @param room object stored
     */
    private void addOwnChatRoom(ChatRoom room, User user) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        user.getOwnChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.OWN_ROOMS_REF).setValue(user.getOwnChatRooms());
    }

    public void addFollowingChat(ChatRoom room, User user) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        user.getFollowedChatRooms().put(room.getId(), room);
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).setValue(user.getFollowedChatRooms());
    }

    public void unFollowChat(ChatRoom room, User user) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        user.getFollowedChatRooms().remove(room.getId());
        userChatsReference.child(userUid).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(room.getId()).removeValue();
    }

    public void removeChatRoom(String roomID, User user) {
        String userUid = FirebaseAuthRepository.getInstance().getFirebaseUser().getUid();
        chatroomsReference.child(roomID).removeValue((databaseError, databaseReference) -> {
            user.getOwnChatRooms().remove(roomID);
            user.getFollowedChatRooms().remove(roomID);
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
        return mFirebaseDatabase.getReference().child(FirebaseContract.CHAT_ROOMS_REF);
    }

    private Query getFollowingRoomsQuery(){
        return userChatsReference.child(FirebaseAuthRepository.getInstance().getFirebaseUser().getUid()).child(FirebaseContract.FOLLOWED_ROOMS_REF);
    }

    private Query getOwnChatRoomsQuery(){
        return userChatsReference.child(FirebaseAuthRepository.getInstance().getFirebaseUser().getUid()).child(FirebaseContract.OWN_ROOMS_REF);
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


    public DatabaseReference getUserChatsReference() {
        return userChatsReference;
    }
}
