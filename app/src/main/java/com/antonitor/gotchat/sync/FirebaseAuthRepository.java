package com.antonitor.gotchat.sync;

import android.util.Log;

import com.antonitor.gotchat.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class FirebaseAuthRepository {

    private static final String LOG_TAG = "AUTHENTICATION_REPO";
    private static FirebaseAuthRepository sInstance;
    private static final Object LOCK = new Object();

    private AuthCallback authCallback;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private User customUser;
    private boolean listeningFlag = false;
    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                FirebaseDatabaseRepository.getInstance().getUserChatsReference().child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    customUser = user;
                                } else {
                                    String tempName = firebaseUser.getEmail().split("@")[0];
                                    customUser = new User(firebaseUser.getUid(), firebaseUser.getEmail(), tempName);
                                    FirebaseDatabaseRepository.getInstance().getUserChatsReference().child(firebaseUser.getUid()).setValue(customUser);
                                }
                                authCallback.login();
                                Log.d(LOG_TAG, "------- LOGGED AS " + customUser.getUserName() + " ---------");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(LOG_TAG, "-------- USER CHATS CANCELLED  -------");
                            }
                        });
            } else {
                Log.d(LOG_TAG, "------- NOT LOGGED IN  ---------");
                authCallback.loggedOut();
            }
        }
    };

    public interface AuthCallback {
        void login();

        void loggedOut();
    }

    public static FirebaseAuthRepository getInstance() {
        Log.d(LOG_TAG, "Getting Firebase Auth Repo");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseAuthRepository();
                Log.d(LOG_TAG, "Made new new Firebase Auth Repo");
            }
        }
        return sInstance;
    }

    private FirebaseAuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void listenAuthentication(AuthCallback callback) {
        this.authCallback = callback;
        Log.d(LOG_TAG, "------- AUTH LISTENER ADDED ---------");
        setListeningFlag(true);
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    public void singOut() {
        firebaseAuth.signOut();
    }

    public void removeAuthListener() {
        Log.d(LOG_TAG, "------- REMOVING AUTH LISTENER ---------");
        if (mAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
            setListeningFlag(false);
        }
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public boolean isListeningFlag() {
        return listeningFlag;
    }

    public void setListeningFlag(boolean listeningFlag) {
        this.listeningFlag = listeningFlag;
    }

    public User getCustomUser() {
        return customUser;
    }
}
