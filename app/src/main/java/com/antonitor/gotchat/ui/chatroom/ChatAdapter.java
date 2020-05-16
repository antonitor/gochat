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
import com.antonitor.gotchat.databinding.ImageChatBinding;
import com.antonitor.gotchat.databinding.MessageChatBinding;
import com.antonitor.gotchat.model.Message;

import com.antonitor.gotchat.sync.FirebaseAuthRepository;
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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CHAT_ADAPTER";
    private static final int TEXT_MESSAGE = 1985, IMAGE_MESSAGE = 2020;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TEXT_MESSAGE) {
            MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.message_chat, parent, false);
            return new MessageViewHolder(dataBinding);
        } else {
            ImageChatBinding imageChatBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.image_chat, parent, false);
            return new ImageViewHolder(imageChatBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXT_MESSAGE:
                ((MessageViewHolder)holder).bind(messages.get(position));
                break;
            case IMAGE_MESSAGE:
                ((ImageViewHolder)holder).bind(messages.get(position));
        }

    }

    /*
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d(TAG, "------------ RECYCLED HOLDER " + holder.getAdapterPosition() + " -------------------");
        Glide.with(holder.itemView.getContext())
                .clear(((MessageViewHolder) holder).dataBinding.photoImageView);
    }
    */

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

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getThumbnail() == null) {
            return TEXT_MESSAGE;
        } else {
            return IMAGE_MESSAGE;
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
            Log.d(TAG, "----------- NO IMAGE MESSAGE " + getAdapterPosition() + " -------------------");
            dataBinding.authorTv.setOnClickListener(view -> {
                if (multiSelect) {
                    //uncomment to enable multiple selection
                    //selectItem(message.getId());
                } else {
                    selectItem();
                }
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
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        final ImageChatBinding dataBinding;
        Message mMessage;

        ImageViewHolder(ImageChatBinding dataBinding) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
        }

        void bind(Message message) {
            this.mMessage = message;
            dataBinding.setMessage(mMessage);
            dataBinding.executePendingBindings();

            Glide.with(mContext)
                    .load(message.getThumbnail())
                    .placeholder(R.drawable.noimage)
                    .centerCrop()
                    .into(dataBinding.photoImageView);
            Log.d(TAG, "----------- YES IMAGE MESSAGE " + getAdapterPosition() + " -------------------");

            if (message.getLocalPhotoUrl() != null && FirebaseAuthRepository.getInstance()
                    .getCustomUser().getUUID() == message.getAuthorUUID() && message.getPhotoUrl() == null) {
                dataBinding.progressBarPictureUpload.setVisibility(View.VISIBLE);
                uploadImage();
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
                        makeSceneTransitionAnimation((Activity) mContext, dataBinding.photoImageView, "image_chat_message");
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