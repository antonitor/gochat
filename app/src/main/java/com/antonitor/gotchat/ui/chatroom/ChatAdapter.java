package com.antonitor.gotchat.ui.chatroom;

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
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends FirebaseRecyclerAdapter {

    private static final String TAG = "CHAT_ADAPTER";
    private final ArrayList<Message> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;
    private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
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
        if (message.getLocalPhotoUrl()!= null) {
            viewHolder.dataBinding.messageTv.setVisibility(View.GONE);
            viewHolder.dataBinding.photoImageView.setVisibility(View.VISIBLE);
            Log.d(TAG, "------------------ GLIDE LOADING LOCAL IMAGE " + holder.getAdapterPosition() +" -------------------");
            Glide.with(viewHolder.itemView.getContext())
                    .load(message.getLocalPhotoUrl())
                    .centerCrop()
                    .into(viewHolder.dataBinding.photoImageView);
            if (message.getPhotoUrl()== null) {
                viewHolder.dataBinding.progressBarPictureUpload.setVisibility(View.VISIBLE);
                viewHolder.uploadImage(message);
            }
         } else {
            Log.d(TAG, "----------- NO IMAGE MESSAGE " + holder.getAdapterPosition() +" -------------------");
            viewHolder.dataBinding.photoImageView.setVisibility(View.GONE);
            viewHolder.dataBinding.messageTv.setVisibility(View.VISIBLE);
        }

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

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d(TAG,"------------ RECYCLED HOLDER "+ holder.getAdapterPosition() + " -------------------");
        Glide.with(holder.itemView.getContext())
                .clear(((MessageViewHolder)holder).dataBinding.photoImageView);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageChatBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_chat, parent, false);
        return new MessageViewHolder(dataBinding);
    }



    class MessageViewHolder extends RecyclerView.ViewHolder {

        final MessageChatBinding dataBinding;

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
            Uri localImageURI = Uri.parse(message.getLocalPhotoUrl());
            UploadTask task = FirebaseStorageRepository.getInstance().uploadFromLocal(localImageURI);
            task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dataBinding.progressBarPictureUpload.setVisibility(View.GONE);
                    Uri downloadUrl = task.getResult().getUploadSessionUri();
                    message.setPhotoUrl(downloadUrl.toString());
                    FirebaseDatabaseRepository.getInstance().updateMessage(message);
                    Log.v(TAG, "SUCCESSFUL BITMAP UPLOAD");
                    Log.v(TAG, "File: " + task.getResult().getMetadata().getName());
                    Log.v(TAG, "Path: " + task.getResult().getMetadata().getPath());
                    Log.v(TAG, "Size: " + task.getResult().getMetadata().getSizeBytes()/1000 + " kb");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    dataBinding.progressBarPictureUpload.setProgress(progress.intValue());
                }
            });
        }
    }
}
