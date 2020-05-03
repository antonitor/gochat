package com.antonitor.gotchat.ui.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityChatRoomBinding;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.vanniktech.emoji.EmojiPopup;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_ACTIVITY";
    private static final int RC_PHOTO_PICKER = 1985;
    private static final int RC_CAMERA_ACTION = 2020;
    ActivityChatRoomBinding dataBinding;
    ChatViewModel viewModel;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.setChatRoom(getIntent().getExtras().getParcelable(getString(R.string.key_chatroom)));
        setTitle(viewModel.getChatRoom().getTitle());

        EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(dataBinding.getRoot())
                .build(dataBinding.messageEditText);
        dataBinding.emojiPicker.setOnClickListener(view -> emojiPopup.toggle());

        //Set up RecyclerView
        adapter = new ChatAdapter(FirebaseDatabaseRepository.getInstance()
                .getMessageListOptions(viewModel.getChatRoom().getId()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        dataBinding.messageRecycleView.setAdapter(adapter);
        dataBinding.messageRecycleView.setLayoutManager(layoutManager);

        /*
        dataBinding.messageRecycleView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) layoutManager.smoothScrollToPosition(dataBinding.messageRecycleView, null, adapter.getItemCount());
        });
        */

        //Handle user imput
        dataBinding.sendButton.setOnClickListener(ChatActivity.this::takePhotoListener);
        dataBinding.photoPickerButton.setOnClickListener(ChatActivity.this::sendImageListener);
        dataBinding.messageEditText.addTextChangedListener(userInputWatcher());
    }

    /**
     * Sets listeners and behaviour for all user input views
     * @return TextWatcher object
     */
    private TextWatcher userInputWatcher() {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        dataBinding.sendButton.setImageResource(R.drawable.ic_send_white_24dp);
                        dataBinding.sendButton.setOnClickListener(ChatActivity.this::sendTextListener);
                        dataBinding.photoPickerButton.setVisibility(View.INVISIBLE);
                    } else {
                        dataBinding.sendButton.setImageResource(R.drawable.ic_camera_grey_24dp);
                        dataBinding.sendButton.setOnClickListener(ChatActivity.this::takePhotoListener);
                        dataBinding.photoPickerButton.setVisibility(View.VISIBLE);
                        dataBinding.photoPickerButton.setOnClickListener(ChatActivity.this::sendImageListener);
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
            };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PHOTO_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri localImage = data.getData();
                    Message tempMsg = new Message(
                            null,
                            viewModel.getChatRoom().getId(),
                            FirebaseDatabaseRepository.getInstance().getFirebaseUser().getPhoneNumber(),
                            null,
                            localImage.toString(),
                            null);
                    Message message= FirebaseDatabaseRepository.getInstance().postMessage(tempMsg);
                    UploadTask task = FirebaseStorageRepository.getInstance().uploadFromLocal(localImage);
                    task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            message.setPhotoUrl(downloadUrl.toString());
                            FirebaseDatabaseRepository.getInstance().updateMessage(message);
                            Log.v(TAG, "SUCCESFULL BITMAP UPLOAD");
                            Log.v(TAG, "File: " + taskSnapshot.getMetadata().getName());
                            Log.v(TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                            Log.v(TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes()/1000 + " kb");
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    });


                    break;
                }
            case RC_CAMERA_ACTION:
                /*
                if (resultCode == RESULT_OK)  {
                    viewModel.setBitmap((Bitmap) data.getExtras().get("data"));
                    viewModel.uploadImage(FirebaseStorageRepository.getInstance()
                            .getMsgImageStorageReference(), );
                    viewModel.getImageUrl().observe(this, url -> {
                        Message message = new Message(viewModel.getChatRoom().getId(),
                                null,
                                FirebaseDatabaseRepository.getInstance().getFirebaseUser().getPhoneNumber(),
                                url);
                        viewModel.postMessage(message);
                    });
                }

                 */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void sendTextListener(View view) {
        Log.d(TAG, "SEND CLICKED --------------------------------");
        String text = dataBinding.messageEditText.getText().toString()
                .replaceFirst("\\s+$", "")
                .replaceFirst("^\\s+", "");
        String roomId = viewModel.getChatRoom().getId();
        String user = FirebaseDatabaseRepository.getInstance().getFirebaseUser()
                .getPhoneNumber();
        Message message = new Message(null, roomId, user, text, null,null);
        dataBinding.messageEditText.setText("");
        FirebaseDatabaseRepository.getInstance().postMessage(message);
        dataBinding.messageRecycleView.smoothScrollToPosition(adapter.getItemCount());
    }

    private void sendImageListener(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, RC_PHOTO_PICKER);
    }

    private void takePhotoListener(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RC_CAMERA_ACTION);
        }
    }
}
