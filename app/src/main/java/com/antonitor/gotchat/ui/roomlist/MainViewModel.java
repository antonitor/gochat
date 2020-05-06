package com.antonitor.gotchat.ui.roomlist;

import android.util.Log;

import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    public MainViewModel() {
        authRepository = FirebaseAuthRepository.getInstance();
        databaseRepository = FirebaseDatabaseRepository.getInstance();
        login = new MutableLiveData<>();
    }

    public void startAuthenticationListener(){
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
    }

    @Override
    protected void onCleared() {
        authRepository.removeAuthListener();
    }

    private void setAuthenticatedUser(FirebaseUser firebaseUser) {
        databaseRepository.getUserChatsReference().child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        customUser= dataSnapshot.getValue(User.class);
                        if (customUser == null) customUser = new User();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
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
}
