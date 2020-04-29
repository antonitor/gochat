package com.antonitor.gotchat.ui.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityAddNewRoomBinding;

public class ChatActivity extends AppCompatActivity {

    ActivityAddNewRoomBinding dataBinding;
    ChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }
}
