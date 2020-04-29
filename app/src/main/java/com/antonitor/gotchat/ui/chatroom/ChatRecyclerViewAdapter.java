package com.antonitor.gotchat.ui.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.MessageChatBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.ui.roomlist.RecyclerViewAdapterFollowing;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder>{

    private List<Message> messageList;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_chat, parent, false);
        return new MessageViewHolder(dataBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        MessageViewHolder messageHolder = holder;
        messageHolder.bind(message);
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }


    /**
     * Swaps the list used by the ChatRecyclerViewAdapter for its weather data. This method is
     * called by {@link ChatActivity} after a load has finished. When this method is called,
     * we assume we have a new set of data, so we call notifyDataSetChanged to tell the RecyclerView
     * to update.
     *
     * @param newMessageList the new list of messages to use as ChatRecyclerViewAdapter's data
     * source
     */
    void swapMessages(final List<Message> newMessageList) {
        // If there was no messages, then recreate all of the list
        if (this.messageList == null) {
            this.messageList = newMessageList;
            notifyDataSetChanged();
        } else {
            /*
             * Otherwise we use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in messageList, where the new list is the new
             * values passed in from the observing the database.
             */
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return messageList.size();
                }

                @Override
                public int getNewListSize() {
                    return newMessageList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return messageList.get(oldItemPosition).getId() ==
                            newMessageList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Message newMessage = newMessageList.get(newItemPosition);
                    Message oldMessage = messageList.get(oldItemPosition);
                    return newMessage.getId() == oldMessage.getId()
                            && newMessage.getTimeStamp().equals(oldMessage.getTimeStamp());
                }
            });
            messageList = newMessageList;
            result.dispatchUpdatesTo(this);
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        MessageChatBinding dataBinding;

        public MessageViewHolder(MessageChatBinding dataBinding) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        public void bind(Message message) {
            dataBinding.setMessage(message);
            dataBinding.executePendingBindings();
        }
    }
}
