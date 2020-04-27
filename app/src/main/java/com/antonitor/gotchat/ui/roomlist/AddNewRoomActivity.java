package com.antonitor.gotchat.ui.roomlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityAddNewRoomBinding;
import com.antonitor.gotchat.sync.FBRDatabaseData;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class AddNewRoomActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddNewRoomActivity.class.getSimpleName();
    private static final int RC_PHOTO_PICKER = 1;
    private static final int RC_CAMERA_ACTION = 2;

    private ActivityAddNewRoomBinding dataBinding;
    private AddNewRoomViewModel viewModel;

    private FirebaseStorage firebaseStorage;
    private StorageReference chatRoomImageStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_room);
        viewModel = new ViewModelProvider(this).get(AddNewRoomViewModel.class);
        viewModel.setImageChosen(false);

        firebaseStorage = FirebaseStorage.getInstance();
        chatRoomImageStorageReference = firebaseStorage.getReference("chatroom_images");

        dataBinding.addPictureButton.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RC_CAMERA_ACTION);
            }
        });

        dataBinding.takePictureButton.setOnClickListener(view -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, RC_PHOTO_PICKER);
        });

        dataBinding.addButton.setOnClickListener(view -> {
            if (!dataBinding.newChatroomEt.getText().toString().trim().equals("")) {
                //final Intent data = new Intent();
                final String title = dataBinding.newChatroomEt.getText().toString();
                final String topic = dataBinding.newTextEt.getText().toString();
                if (viewModel.isImageChosen()) {
                    dataBinding.progressBarPictureUpload.setVisibility(View.VISIBLE);
                    Observer<String> imageUrlObserver = imageUrl -> {
                        FBRDatabaseData.getInstance().newChatRoom(title, title, topic, imageUrl);
                        /*
                        data.putExtra(getString(R.string.extra_new_image), imageUrl);
                        data.putExtra(getString(R.string.extra_new_chatroom), title);
                        data.putExtra(getString(R.string.extra_new_topic), topic);
                        setResult(RESULT_OK, data);
                        */
                        dataBinding.progressBarPictureUpload.setVisibility(View.INVISIBLE);
                        finish();
                    };
                    viewModel.getImageUrl().observe(AddNewRoomActivity.this, imageUrlObserver);
                    storeImage();
                } else {
                    FBRDatabaseData.getInstance().newChatRoom(title, title, topic, null);
                    /*
                    data.putExtra(getString(R.string.extra_new_chatroom), title);
                    data.putExtra(getString(R.string.extra_new_topic), topic);
                    setResult(RESULT_OK, data);
                    */
                    finish();
                }
            } else {
                Toast.makeText(AddNewRoomActivity.this, "Imprescindible añadir un #Hashtag y una descripcion!", Toast.LENGTH_SHORT).show();
            }
        });

        dataBinding.cancelButton.setOnClickListener(view -> {
            finish();
        });
    }

    private void storeImage() {
        if (viewModel.isBitmap()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            viewModel.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final String randomImageName = "image-" + (new Date().getTime());
            final StorageReference photoRef = chatRoomImageStorageReference.child(randomImageName + ".jpg");
            UploadTask uploadTask = photoRef.putBytes(baos.toByteArray());
            uploadTask.addOnSuccessListener(this, taskSnapshot -> {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                viewModel.setImageUrl(downloadUrl.toString());
            });
        } else {
            StorageReference photoRef = chatRoomImageStorageReference.child(viewModel.getUri().getLastPathSegment());
            photoRef.putFile(viewModel.getUri()).addOnSuccessListener(this, taskSnapshot -> {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                viewModel.setImageUrl(downloadUrl.toString());
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PHOTO_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    viewModel.setUri(selectedImageUri);
                    viewModel.setImageChosen(true);
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(dataBinding.newChatroomIv);
                }
                break;
            case RC_CAMERA_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    viewModel.setBitmap(imageBitmap);
                    viewModel.setImageChosen(true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    Glide.with(this)
                            .asBitmap()
                            .load(baos.toByteArray())
                            .into(dataBinding.newChatroomIv);
                }
        }
    }
}
