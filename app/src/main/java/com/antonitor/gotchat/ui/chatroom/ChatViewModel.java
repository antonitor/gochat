package com.antonitor.gotchat.ui.chatroom;

import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private static final String LOG_TAG = "CHAT_VIEW_MODEL";

    private ChatRoom chatRoom;
    private MutableLiveData<List<Message>> messages;
    private FirebaseDatabaseRepository repository = FirebaseDatabaseRepository.getInstance();

    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = new MutableLiveData<>();
            loadMessages();
        }
        return messages;
    }

    private void loadMessages() {
        repository.addMessagesListener(chatRoom.getId(),
                new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Message>() {
                    @Override
                    public void onSuccess(List<Message> result) {
                        messages.setValue(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        messages.setValue(null);
                    }
                });
    }

    @Override
    protected void onCleared() {
        repository.removeMessageListener();
        Log.d(LOG_TAG, "-------- CHAT VIEWMODEL CLEARED --- STOP LISTENING MESSAGES -------");
    }

    ChatRoom getChatRoom() {
        return chatRoom;
    }

    void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
