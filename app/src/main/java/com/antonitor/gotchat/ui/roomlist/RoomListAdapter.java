package com.antonitor.gotchat.ui.roomlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ItemRoomBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RoomListAdapter extends FirebaseRecyclerAdapter {

    private OnLongClickListener onLongClickListener;
    private OnRoomClickListener onRoomClickListener;
    private ItemRoomBinding itemBinding;

    public interface OnLongClickListener {
        void onLongClick(ChatRoom room);
    }

    public RoomListAdapter(@NonNull FirebaseRecyclerOptions options, OnLongClickListener onLongClickListener, OnRoomClickListener onRoomClickListener) {
        super(options);
        this.onLongClickListener = onLongClickListener;
        this.onRoomClickListener = onRoomClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                    int position, @NonNull Object model) {
        ChatRoom room = (ChatRoom) model;
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.bind(room);
        roomViewHolder.itemBinding.setImage(room.getImageUrl());
        roomViewHolder.itemBinding.followButton
                .setOnClickListener(view -> onLongClickListener.onLongClick(room));
        roomViewHolder.itemBinding.tvTitle
                .setOnClickListener(view -> onRoomClickListener.onRoomClicked(room));
    }

    public interface OnRoomClickListener {
        void onRoomClicked(ChatRoom room);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_room, parent, false);
        return new RoomViewHolder(itemBinding);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        ItemRoomBinding itemBinding;

        RoomViewHolder(@NonNull ItemRoomBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(ChatRoom room) {
            itemBinding.setChatroom(room);
            itemBinding.executePendingBindings();
        }
    }

}
