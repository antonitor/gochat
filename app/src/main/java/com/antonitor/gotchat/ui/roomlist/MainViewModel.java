package com.antonitor.gotchat.ui.roomlist;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private static final String LOG_TAG = "MAIN_VIEW_MODEL";
    private FirebaseAuthRepository authRepository;
    private FirebaseDatabaseRepository databaseRepository;
    private MutableLiveData<Boolean> login;
    private MutableLiveData<List<ChatRoom>> chatRooms;
    private MutableLiveData<List<ChatRoom>> subscribedChatRooms;
    private MutableLiveData<List<ChatRoom>> friendChatRooms;

    public MainViewModel() {
        authRepository = FirebaseAuthRepository.getInstance();
        databaseRepository = FirebaseDatabaseRepository.getInstance();
        login = new MutableLiveData<>();
    }

    public void startAuthenticationListener() {
        if (!authRepository.isListeningFlag())
            authRepository.listenAuthentication(new FirebaseAuthRepository.AuthCallback() {
                @Override
                public void login() {
                    Log.d(LOG_TAG, "---------------- LOGIN ----------------");
                    login.setValue(true);
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
        databaseRepository.removeRoomsListener();
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "---------- CLEARED -----------");
        authRepository.removeAuthListener();
        databaseRepository.removeRoomsListener();
    }

    public LiveData<Boolean> getLogin() {
        return login;
    }

    public LiveData<List<ChatRoom>> getAllChatRooms() {
        if (chatRooms == null) {
            chatRooms = new MutableLiveData<>();
            loadAllRooms();
        }
        return chatRooms;
    }

    public LiveData<List<ChatRoom>> getSubscribedChatRooms() {
        if (subscribedChatRooms == null) {
            subscribedChatRooms = new MutableLiveData<>();
            loadSubscribedRooms();
        }
        return subscribedChatRooms;
    }

    public LiveData<List<ChatRoom>> getFriendChatRooms() {
        if (friendChatRooms == null) {
            friendChatRooms = new MutableLiveData<>();
            loadFriendRooms();
        }
        return friendChatRooms;
    }

    private void loadAllRooms() {
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

    public void stopListeningAllRooms() {
        databaseRepository.removeRoomsListener();
    }

    private void loadSubscribedRooms() {
        databaseRepository.addSubscribedRoomsListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ChatRoom>() {
            @Override
            public void onSuccess(List<ChatRoom> result) {
                subscribedChatRooms.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                Log.d(LOG_TAG, "------------ ERROR LOADING ROOMS ---------------");
                subscribedChatRooms.setValue(null);
            }
        });
    }

    public void stopListeningSubscribedRooms() {
        databaseRepository.removeSubscribedRoomsListener();
    }

    private void loadFriendRooms() {
        databaseRepository.addFriendRoomsListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<ChatRoom>() {
            @Override
            public void onSuccess(List<ChatRoom> result) {
                friendChatRooms.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                Log.d(LOG_TAG, "------------ ERROR LOADING ROOMS ---------------");
                friendChatRooms.setValue(null);
            }
        });
    }

    public void stopListeningFriendRooms() {
        databaseRepository.removeFriendRoomsListener();
    }

    public User getCustomUser() {
        return FirebaseAuthRepository.getInstance().getCustomUser();
    }

}
