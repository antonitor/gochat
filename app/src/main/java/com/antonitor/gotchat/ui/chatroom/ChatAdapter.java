package com.antonitor.gotchat.ui.chatroom;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.MessageChatBinding;
import com.antonitor.gotchat.model.Message;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends FirebaseRecyclerAdapter {

    private static final String TAG = "CHAT_RECYCLER_VIEW";
    private ArrayList<Message> selectedItems = new ArrayList<Message>();
    private boolean multiSelect = false;
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add("Profile");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (Message message : selectedItems) {

            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };


    ChatAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {
        Message message = (Message) model;
        MessageViewHolder viewHolder = (MessageViewHolder) holder;
        viewHolder.bind(message);
        Glide.with(viewHolder.dataBinding.photoImageView.getContext())
                .load(message.getLocalPhotoUrl())
                .into(viewHolder.dataBinding.photoImageView);


        viewHolder.dataBinding.authorTv.setOnClickListener(view -> {
            if (multiSelect) {
                //uncomment to enable multiple selection
                //viewHolder.selectItem(message.getId());
            } else {
                viewHolder.selectItem(message);
            }
        });
        viewHolder.update(message);

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_chat, parent, false);
        return new MessageViewHolder(dataBinding);
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

        void selectItem(Message message) {
            if (multiSelect) {
                if (selectedItems.contains(message)) {
                    selectedItems.remove(message);
                    dataBinding.authorTv.setBackgroundColor(Color.WHITE);
                    //dataBinding.authorTv.getPaint().setUnderlineText(false);
                } else {
                    selectedItems.add(message);
                    //dataBinding.authorTv.getPaint().setUnderlineText(true);
                    dataBinding.authorTv.setBackgroundColor(Color.YELLOW);
                }
            }
        }

        void update(final Message message) {
            if (selectedItems.contains(message)) {
                //dataBinding.authorTv.getPaint().setUnderlineText(true);
                dataBinding.authorTv.setBackgroundColor(Color.YELLOW);
                //itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                //dataBinding.authorTv.getPaint().setUnderlineText(false);
                dataBinding.authorTv.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(message);
                    return true;
                }
            });
        }

        void uploadImage(Message message){

        }

    }
}
