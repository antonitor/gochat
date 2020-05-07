package com.antonitor.gotchat.ui.roomlist;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private static final String LOG_TAG = "MAIN_VIEW_MODEL";
    private FirebaseAuthRepository authRepository;
    private FirebaseDatabaseRepository databaseRepository;
    private User customUser;
    private MutableLiveData<Boolean> login;
    private MutableLiveData<List<ChatRoom>> chatRooms;

    public MainViewModel() {
        authRepository = FirebaseAuthRepository.getInstance();
        databaseRepository = FirebaseDatabaseRepository.getInstance();
        login = new MutableLiveData<>();
    }

    public void startAuthenticationListener() {
        if (!authRepository.isListeningFlag())
            authRepository.listenAuthentication(new FirebaseAuthRepository.AuthCallback() {
                @Override
                public void login(FirebaseUser firebaseUser) {
                    Log.d(LOG_TAG, "---------------- LOGIN ----------------");
                    login.setValue(true);
                    setAuthenticatedUser(firebaseUser);
                }

                @Override
                public void loggedOut() {
                    Log.d(LOG_TAG, "---------------- LOGGED OUT ------------");
                    login.setValue(false);
                }
            });
    }

    public void singOut() {
        authRepository.singOut();
        databaseRepository.removeRoomsListener(); //<-- NEEDED??
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "---------- CLEARED -----------");
        authRepository.removeAuthListener();
        databaseRepository.removeRoomsListener();
    }

    private void setAuthenticatedUser(FirebaseUser firebaseUser) {
        databaseRepository.getUserChatsReference().child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        customUser = dataSnapshot.getValue(User.class);
                        if (customUser == null) customUser = new User();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(LOG_TAG, "-------- USER CHATS CANCELLED ----- SET LOGIN FALSE -------");
                        login.setValue(false);
                        Log.d(LOG_TAG, databaseError.getMessage());
                    }
                });
    }

    public User getCustomUser() {
        return customUser;
    }

    public LiveData<Boolean> getLogin() {
        return login;
    }

    public LiveData<List<ChatRoom>> getChatRooms() {
        if (chatRooms == null) {
            chatRooms = new MutableLiveData<>();
            loadRooms();
        }
        return chatRooms;
    }

    private void loadRooms() {
        databaseRepository.addRoomsListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ChatRoom>() {
            @Override
            public void onSuccess(List<ChatRoom> result) {
                chatRooms.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                Log.d(LOG_TAG, "------------ ERROR LOADING ROOMS ---------------");
                chatRooms.setValue(null);
            }
        });
    }

    public void stopListeningRooms() {
        databaseRepository.removeRoomsListener();
    }

    public boolean areWeListeningToAuthRepo() {
        return authRepository.isListeningFlag();
    }

}
