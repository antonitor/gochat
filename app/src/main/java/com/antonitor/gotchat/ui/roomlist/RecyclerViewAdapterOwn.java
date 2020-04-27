package com.antonitor.gotchat.ui.roomlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ItemRoomBinding;
import com.antonitor.gotchat.databinding.OwnRoomBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterOwn extends FirebaseRecyclerAdapter {

    private OnCloseClickListener onCloseClickListener;
    private OwnRoomBinding itemBinding;

    public interface OnCloseClickListener {
        void onCloseClicked(ChatRoom room);
    }

    public RecyclerViewAdapterOwn(@NonNull FirebaseRecyclerOptions options, OnCloseClickListener onClickListener) {
        super(options);
        this.onCloseClickListener = onClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                    int position, @NonNull Object model) {
        ChatRoom room = (ChatRoom) model;
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.bind(room);
        roomViewHolder.itemBinding.followButton
                .setOnClickListener(view -> onCloseClickListener.onCloseClicked(room));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.own_room, parent, false);
        return new RoomViewHolder(itemBinding);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        OwnRoomBinding itemBinding;

        RoomViewHolder(@NonNull OwnRoomBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(ChatRoom room) {
            itemBinding.setChatroom(room);
            itemBinding.executePendingBindings();
        }
    }

}
