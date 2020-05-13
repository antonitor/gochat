package com.antonitor.gotchat.ui.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.MessageChatBinding;
import com.antonitor.gotchat.model.Message;

import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.antonitor.gotchat.ui.image.ImageActivity;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final String TAG = "CHAT_ADAPTER";
    private Context mContext;
    private List<Message> messages;
    private final List<Message> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;
    private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add(R.string.profile);
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

    public ChatAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_chat, parent, false);
        return new MessageViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d(TAG, "------------ RECYCLED HOLDER " + holder.getAdapterPosition() + " -------------------");
        Glide.with(holder.itemView.getContext())
                .clear(((MessageViewHolder) holder).dataBinding.photoImageView);
    }

    @Override
    public int getItemCount() {
        if (messages == null) return 0;
        return messages.size();
    }

    void swapMessages(final List<Message> newList) {
        if (messages == null) {
            messages = newList;
            notifyDataSetChanged();
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return messages.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return messages.get(oldItemPosition).getMessageUUID() ==
                            newList.get(newItemPosition).getMessageUUID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Message newMessage = newList.get(newItemPosition);
                    Message oldMessage = messages.get(oldItemPosition);
                    return newMessage.getMessageUUID() == oldMessage.getMessageUUID()
                            && newMessage.getTimeStamp().equals(oldMessage.getTimeStamp());
                }
            });
            messages = newList;
            result.dispatchUpdatesTo(this);
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        final MessageChatBinding dataBinding;
        Message mMessage;

        MessageViewHolder(MessageChatBinding dataBinding) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        void bind(Message message) {
            this.mMessage = message;
            dataBinding.setMessage(mMessage);
            dataBinding.executePendingBindings();
            if (message.getLocalPhotoUrl() != null) {
                dataBinding.messageTv.setVisibility(View.GONE);
                dataBinding.photoImageView.setVisibility(View.VISIBLE);
                Log.d(TAG, "----------- GLIDE LOADING LOCAL IMAGE " + getAdapterPosition() + " ----------");
                Glide.with(mContext)
                        .load(message.getLocalPhotoUrl())
                        .centerCrop()
                        .into(dataBinding.photoImageView);
                if (message.getPhotoUrl() == null) {
                    dataBinding.progressBarPictureUpload.setVisibility(View.VISIBLE);
                    uploadImage();
                }
            } else if (message.getPhotoUrl() != null) {
                Log.d(TAG, "----------- GLIDE LOADING CLOUD IMAGE " + getAdapterPosition() + " ---------");
                //DOWNLOAD IMAGE
            } else {
                Log.d(TAG, "----------- NO IMAGE MESSAGE " + getAdapterPosition() + " -------------------");
                dataBinding.photoImageView.setVisibility(View.GONE);
                dataBinding.messageTv.setVisibility(View.VISIBLE);
            }

            dataBinding.authorTv.setOnClickListener(view -> {
                if (multiSelect) {
                    //uncomment to enable multiple selection
                    //selectItem(message.getId());
                } else {
                    selectItem();
                }
            });
            dataBinding.photoImageView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                intent.putExtra(mContext.getString(R.string.extra_image_message), message);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)mContext, dataBinding.photoImageView, "image_chat_message");
                view.getContext().startActivity(intent, options.toBundle());
            });
            update();
        }

        void selectItem() {
            if (multiSelect) {
                if (selectedItems.contains(mMessage)) {
                    selectedItems.remove(mMessage);
                    dataBinding.authorTv.setBackgroundColor(Color.WHITE);
                    //dataBinding.authorTv.getPaint().setUnderlineText(false);
                } else {
                    selectedItems.add(mMessage);
                    //dataBinding.authorTv.getPaint().setUnderlineText(true);
                    dataBinding.authorTv.setBackgroundColor(Color.YELLOW);
                }
            }
        }

        void update() {
            if (selectedItems.contains(mMessage)) {
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
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem();
                    return true;
                }
            });
        }

        void uploadImage() {
            Uri localImageURI = Uri.parse(mMessage.getLocalPhotoUrl());
            FirebaseStorageRepository.getInstance().uploadFromLocal(localImageURI,
                    FirebaseStorageRepository.getInstance().getMsgImgRef(),
                    new FirebaseStorageRepository.UploadCallback() {
                        @Override
                        public void onComplete(String downloadUrl) {
                            dataBinding.progressBarPictureUpload.setVisibility(View.GONE);
                            mMessage.setPhotoUrl(downloadUrl);
                            FirebaseDatabaseRepository.getInstance().updateMessage(mMessage);
                        }

                        @Override
                        public void onProgress(Double progress) {
                            dataBinding.progressBarPictureUpload.setProgress(progress.intValue());
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
        }
    }
}