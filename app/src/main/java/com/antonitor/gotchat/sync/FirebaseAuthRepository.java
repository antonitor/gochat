package com.antonitor.gotchat.sync;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class FirebaseAuthRepository {

    private static final String LOG_TAG = "AUTHENTICATION_REPO";
    private static FirebaseAuthRepository sInstance;
    private static final Object LOCK = new Object();

    private AuthCallback authCallback;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Log.d(LOG_TAG, "------- LOGGED AS " + firebaseUser.getPhoneNumber() + " ---------");
                authCallback.login(firebaseUser);
            } else {
                Log.d(LOG_TAG, "------- NOT LOGGED IN  ---------");
                authCallback.loggedOut();
            }
        }
    };
    private boolean listeningFlag =false ;


    public interface AuthCallback {
        void login(FirebaseUser user);
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
        if (mAuthStateListener!=null) {
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
}
