package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class FirebaseAuthHelper {

    private static final String LOG_TAG = FirebaseDatabaseRepository.class.getSimpleName();
    private static FirebaseAuthHelper sInstance;
    private static final Object LOCK = new Object();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private User user;

    public static FirebaseAuthHelper getInstance() {
        Log.d(LOG_TAG, "Getting Firebase Sync Repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseAuthHelper();
                Log.d(LOG_TAG, "Made new new Firebase Sync Repository");
            }
        }
        return sInstance;
    }

    private FirebaseAuthHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        setUpUserChatRooms();
    }

    private void setUpUserChatRooms() {
        FirebaseDatabaseRepository.getInstance().getUserChatsReference()
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getOwnChatRooms() == null)
                        user.setOwnChatRooms(new HashMap<>());
                    if (user.getFollowedChatRooms() == null)
                        user.setFollowedChatRooms(new HashMap<>());
                } else {
                    user = new User(new HashMap<>(), new HashMap<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, databaseError.getMessage());
            }
        });
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }
}
