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

public class MainRecyclerViewAdapter extends FirebaseRecyclerAdapter {

    private RoomOnClickListener onClickListener;
    private ItemRoomBinding itemBinding;

    public interface RoomOnClickListener {
        void onRoomClicked();
    }

    public MainRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions options, RoomOnClickListener onClickListener) {
        super(options);
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {
        ((RoomViewHolder) holder).bind((ChatRoom)model);
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
