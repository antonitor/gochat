package com.antonitor.gotchat.ui.roomlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FollowedRoomBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterFollowing extends FirebaseRecyclerAdapter {

    private OnUnfollowClickListener onClickListener;
    private FollowedRoomBinding itemBinding;

    public interface OnUnfollowClickListener {
        void onUnfollowClicked(ChatRoom room);
    }

    public RecyclerViewAdapterFollowing(@NonNull FirebaseRecyclerOptions options, OnUnfollowClickListener onClickListener) {
        super(options);
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                    int position, @NonNull Object model) {
        ChatRoom room = (ChatRoom) model;
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.bind(room);
        roomViewHolder.itemBinding.setImage(room.getImageUrl());
        roomViewHolder.itemBinding.followButton
                .setOnClickListener(view -> onClickListener.onUnfollowClicked(room));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.followed_room, parent, false);
        return new RoomViewHolder(itemBinding);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        FollowedRoomBinding itemBinding;

        RoomViewHolder(@NonNull FollowedRoomBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(ChatRoom room) {
            itemBinding.setChatroom(room);
            itemBinding.executePendingBindings();
        }
    }

}
