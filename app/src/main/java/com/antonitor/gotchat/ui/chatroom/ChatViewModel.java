package com.antonitor.gotchat.ui.chatroom;

import com.antonitor.gotchat.model.ChatRoom;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "CHAT_VIEW_MODEL";

    private ChatRoom chatRoom;

    ChatRoom getChatRoom() {
        return chatRoom;
    }

    void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
