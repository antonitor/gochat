package com.antonitor.gotchat.ui.roomlist;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ItemRoomBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RoomListAdapterOwn extends FirebaseRecyclerAdapter {

    private OnRoomClickListener onRoomClickListener;
    private ItemRoomBinding itemBinding;
    private boolean multiSelect = false;
    private ArrayList<String> selectedItems = new ArrayList<String>();
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (String roomID : selectedItems) {
                FirebaseDatabaseRepository.getInstance().removeChatRoom(roomID);
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

    public interface OnRoomClickListener {
        void onRoomClicked(ChatRoom room);
    }


    public RoomListAdapterOwn(@NonNull FirebaseRecyclerOptions options, OnRoomClickListener onRoomClickListener) {
        super(options);
        this.onRoomClickListener = onRoomClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                    int position, @NonNull Object model) {
        ChatRoom room = (ChatRoom) model;
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.bind(room);
        roomViewHolder.itemBinding.setImage(room.getImageUrl());
        roomViewHolder.itemBinding.getRoot().setOnClickListener(view -> {
                    if (multiSelect) {
                        //uncomment to enable multiple selection
                        //roomViewHolder.selectItem(room.getId());
                    } else {
                        onRoomClickListener.onRoomClicked(room);
                    }
                });
        roomViewHolder.update(room.getId());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_room, parent, false);
        return new RoomViewHolder(itemBinding);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {

        ItemRoomBinding itemBinding;

        RoomViewHolder(@NonNull ItemRoomBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(ChatRoom room) {
            itemBinding.setChatroom(room);
            itemBinding.executePendingBindings();
        }

        void selectItem(String itemID) {
            if (multiSelect) {
                if (selectedItems.contains(itemID)) {
                    selectedItems.remove(itemID);
                    itemView.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(itemID);
                    itemView.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void update(final String roomID) {
            if (selectedItems.contains(roomID)) {
                itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(roomID);
                    return true;
                }
            });
        }
    }

}
