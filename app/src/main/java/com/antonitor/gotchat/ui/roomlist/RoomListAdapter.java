package com.antonitor.gotchat.ui.roomlist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ItemRoomBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder> {

    private Context mContext;
    private User owner;
    private List<ChatRoom> chatRooms = new ArrayList<>();
    private final OnRoomClickListener onRoomClickListener;
    enum roomTypes {ALL, SUBSCRIBED, FRIENDS}
    private roomTypes roomType;

    private final ArrayList<ChatRoom> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;
    private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            switch (roomType) {
                case ALL:
                    menu.add("SUBSCRIBE");
                    break;
                case SUBSCRIBED:
                    menu.add("UNSUBSCRIBE");
                    break;
                case FRIENDS:
                    menu.add("DELETE");
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (roomType) {
                case ALL:
                    for (ChatRoom room : selectedItems) {
                        FirebaseDatabaseRepository.getInstance().addFollowingChat(room);
                    }
                    break;
                case SUBSCRIBED:
                    for (ChatRoom room : selectedItems) {
                        FirebaseDatabaseRepository.getInstance().unFollowChat(room);
                    }
                    break;
                case FRIENDS:
                    for (ChatRoom room : selectedItems) {
                        FirebaseDatabaseRepository.getInstance().removeChatRoom(room.getId());
                    }
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


    RoomListAdapter(Context context, roomTypes roomType, OnRoomClickListener onRoomClickListener, User owner) {
        this.mContext = context;
        this.roomType = roomType;
        this.onRoomClickListener = onRoomClickListener;
        this.owner = owner;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoomBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_room, parent, false);
        return new RoomViewHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(chatRooms.get(position));
    }


    @Override
    public int getItemCount() {
        if (chatRooms == null) return 0;
        return chatRooms.size();
    }

    void swapChatRooms(final List<ChatRoom> newList) {
        if (chatRooms == null) {
            chatRooms = newList;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return chatRooms.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return chatRooms.get(oldItemPosition).getId() ==
                            newList.get(newItemPosition).getId();
                }
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ChatRoom newChatRoom = newList.get(newItemPosition);
                    ChatRoom oldChatRoom = chatRooms.get(oldItemPosition);
                    return newChatRoom.getId() == oldChatRoom.getId()
                            && newChatRoom.getTimestamp().equals(oldChatRoom.getTimestamp());
                }
            });
            chatRooms = newList;
            result.dispatchUpdatesTo(this);
        }
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {

        final ItemRoomBinding itemBinding;

        RoomViewHolder(@NonNull ItemRoomBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        void bind(ChatRoom room) {
            itemBinding.setChatroom(room);
            itemBinding.executePendingBindings();
            itemBinding.setImage(room.getImageUrl());
            itemBinding.getRoot().setOnClickListener(view -> {
                if (multiSelect) {
                    //uncomment to enable multiple selection
                    //roomViewHolder.selectItem(room.getId());
                } else {
                    onRoomClickListener.onRoomClicked(room);
                }
            });
            update(room);
        }

        void selectItem(ChatRoom room) {
            if (multiSelect) {
                if (selectedItems.contains(room)) {
                    selectedItems.remove(room);
                    itemView.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(room);
                    itemView.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void update(final ChatRoom room) {
            if (selectedItems.contains(room)) {
                itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(view -> {
                ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(room);
                return true;
            });
        }
    }

}
