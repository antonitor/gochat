package com.antonitor.gotchat.ui.chatroom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.MessageChatBinding;
import com.antonitor.gotchat.model.Message;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRecyclerViewAdapter extends FirebaseRecyclerAdapter {

    private static final String TAG = "CHAT_RECYCLER_VIEW";
    private OnMessageClickListener onMessageClickListener;

    ChatRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions options, OnMessageClickListener onMessageClickListener) {
        super(options);
        this.onMessageClickListener = onMessageClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {
        Message message = (Message) model;
        MessageViewHolder viewHolder = (MessageViewHolder) holder;
        viewHolder.bind(message);
        Log.d(TAG, "MESSAGE HAS IMAGE?" + message.getPhotoUrl());
        Glide.with(viewHolder.dataBinding.photoImageView.getContext())
                .load(message.getPhotoUrl())
                .into(viewHolder.dataBinding.photoImageView);
        viewHolder.dataBinding.getRoot()
                .setOnClickListener(view -> onMessageClickListener.onMessageClicked(message));
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_chat, parent, false);
        return new MessageViewHolder(dataBinding);
    }


    public interface OnMessageClickListener {
        void onMessageClicked(Message message);
    }


    class MessageViewHolder extends RecyclerView.ViewHolder {

        MessageChatBinding dataBinding;

        MessageViewHolder(MessageChatBinding dataBinding) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        void bind(Message message) {
            dataBinding.setMessage(message);
            dataBinding.executePendingBindings();
        }
    }
}
