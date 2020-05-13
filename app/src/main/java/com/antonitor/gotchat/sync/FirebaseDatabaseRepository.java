package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ValueEventListener roomsListener;
    private ValueEventListener subscribedRoomsListener;
    private ValueEventListener friendRoomsListener;

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
        roomsListener = getRoomsValueEventListener(callback);
        chatroomsReference.addValueEventListener(roomsListener);
    }

    public void removeRoomsListener() {
        if (roomsListener!=null)
            chatroomsReference.removeEventListener(roomsListener);
    }

    public void addSubscribedRoomsListener(FirebaseDatabaseRepositoryCallback<ChatRoom> callback) {
        subscribedRoomsListener = getRoomsValueEventListener(callback);
        userChatsReference.child(FirebaseAuthRepository.getInstance().getCustomUser().getUUID())
                .child(FirebaseContract.FOLLOWED_ROOMS_REF).addValueEventListener(subscribedRoomsListener);
    }

    public void removeSubscribedRoomsListener() {
        if (subscribedRoomsListener!=null)
            userChatsReference.child(FirebaseAuthRepository.getInstance().getCustomUser().getUUID())
                    .child(FirebaseContract.FOLLOWED_ROOMS_REF).removeEventListener(subscribedRoomsListener);
    }

    public void addFriendRoomsListener(FirebaseDatabaseRepositoryCallback<ChatRoom> callback) {
        friendRoomsListener = getRoomsValueEventListener(callback);
        userChatsReference.child(FirebaseAuthRepository.getInstance().getCustomUser().getUUID())
                .child(FirebaseContract.OWN_ROOMS_REF).addValueEventListener(friendRoomsListener);
    }

    public void removeFriendRoomsListener() {
        if (friendRoomsListener!=null)
            userChatsReference.child(FirebaseAuthRepository.getInstance().getCustomUser().getUUID())
                    .child(FirebaseContract.OWN_ROOMS_REF).removeEventListener(friendRoomsListener);
    }

    private ValueEventListener getRoomsValueEventListener(FirebaseDatabaseRepositoryCallback<ChatRoom> callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatRoom> roomsList = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    roomsList.add(item.getValue(ChatRoom.class));
                }
                callback.onSuccess(roomsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(new Exception(databaseError.getMessage()));
            }
        };
    }


    /**
     * Add new Chat Room stored as /chatrooms/roomID
     * @param title room title
     * @param topic room topic
     * @param photoUrl room photourl
     */
    public void newChatRoom(String title, String topic, String photoUrl) {
        chatroomsReference.push().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                ChatRoom room = new ChatRoom(
                        key,
                        title,
                        topic,
                        FirebaseAuthRepository.getInstance().getCustomUser().getUUID(),
                        photoUrl);
                chatroomsReference.child(key).setValue(room);
                addOwnChatRoom(room);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, "CANCELLED CREATING A NEW CHATROOM");
            }
        });
    }

    /**
     * Add this chatroom to the OWN CHAT ROOMS LIST
     * Stored as /userchats/userID/ownChatRooms/roomID
     * @param room object stored
     */
    private void addOwnChatRoom(ChatRoom room) {
        User user = FirebaseAuthRepository.getInstance().getCustomUser();
        user.getOwnChatRooms().put(room.getId(), room);
        userChatsReference.child(user.getUUID()).child(FirebaseContract.OWN_ROOMS_REF)
                .setValue(user.getOwnChatRooms());
    }

    public void addFollowingChat(ChatRoom room) {
        User user = FirebaseAuthRepository.getInstance().getCustomUser();
        user.getFollowedChatRooms().put(room.getId(), room);
        userChatsReference.child(user.getUUID()).child(FirebaseContract.FOLLOWED_ROOMS_REF)
                .setValue(user.getFollowedChatRooms());
    }

    public void unFollowChat(ChatRoom room) {
        User user = FirebaseAuthRepository.getInstance().getCustomUser();
        user.getFollowedChatRooms().remove(room.getId());
        userChatsReference.child(user.getUUID()).child(FirebaseContract.FOLLOWED_ROOMS_REF)
                .child(room.getId()).removeValue();
    }

    public void removeChatRoom(String roomID) {
        User user = FirebaseAuthRepository.getInstance().getCustomUser();
        chatroomsReference.child(roomID).removeValue((databaseError, databaseReference) -> {
            user.getOwnChatRooms().remove(roomID);
            user.getFollowedChatRooms().remove(roomID);
            userChatsReference.child(user.getUUID()).child(FirebaseContract.OWN_ROOMS_REF).child(roomID).removeValue();
            userChatsReference.child(user.getUUID()).child(FirebaseContract.FOLLOWED_ROOMS_REF).child(roomID).removeValue();
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

    public DatabaseReference getUserChatsReference() {
        return userChatsReference;
    }
}
