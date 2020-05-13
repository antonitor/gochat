package com.antonitor.gotchat.ui.newroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityAddNewRoomBinding;
import com.antonitor.gotchat.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.antonitor.gotchat.utilities.Utilities.bitmapByteArray;

public class AddNewRoomActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;
    private static final int RC_CAMERA_ACTION = 2;

    private ActivityAddNewRoomBinding dataBinding;
    private AddNewRoomViewModel viewModel;
    private Observer<Boolean> loadingObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isLoading) {
            if(isLoading) {
                disableViewsWhileLoading();
                Observer<Double> progress = aDouble -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dataBinding.progressBarPictureUpload.setProgress(aDouble.intValue(), true);
                    }
                };
                viewModel.getUploadProgress().observe(AddNewRoomActivity.this, progress);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_room);
        viewModel = new ViewModelProvider(this).get(AddNewRoomViewModel.class);
        User user = getIntent().getExtras().getParcelable(getString(R.string.extra_userowner));
        viewModel.setOwner(user);
        viewModel.getIsLoading().observe(this, loadingObserver);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RC_CAMERA_ACTION);
        }
    }

    public void selectPicture(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, RC_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null) return;
        switch (requestCode) {
            case RC_PHOTO_PICKER:
                if (resultCode == RESULT_OK) {
                    viewModel.setLocalImageUri(data.getData());
                    viewModel.setImageChosen();
                    Glide.with(this)
                            .load(viewModel.getLocalImageUri())
                            .into(dataBinding.newChatroomIv);
                }
                break;
            case RC_CAMERA_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if(data.getExtras()==null || !data.getExtras().containsKey("data")) return;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    viewModel.setBitmap(imageBitmap);
                    viewModel.setImageChosen();
                    Glide.with(this)
                            .asBitmap()
                            .load(bitmapByteArray(imageBitmap))
                            .apply(new RequestOptions().circleCrop())
                            .into(dataBinding.newChatroomIv);
                }
        }
    }

    public void addNewRoom(View view) {
        if (!dataBinding.newChatroomEt.getText().toString().trim().equals("")
                && !dataBinding.newTextEt.getText().toString().trim().equals("")) {
            final String title = dataBinding.newChatroomEt.getText().toString();
            final String topic = dataBinding.newTextEt.getText().toString();
            if (viewModel.isImageChosen()) {
                Observer<String> imageUrlObserver = imageURl -> {
                    viewModel.newChatRoom(title, topic, imageURl, viewModel.getOwner());
                    finish();
                };
                viewModel.getImageUrl().observe(this, imageUrlObserver);
                viewModel.uploadImage();
            } else {
                Toast.makeText(this, "Please, chose and image!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please, type in title and topic!", Toast.LENGTH_LONG).show();
        }
    }

    private void disableViewsWhileLoading() {
        dataBinding.progressBarPictureUpload.setVisibility(View.VISIBLE);
        dataBinding.takePictureButton.setEnabled(false);
        dataBinding.addPictureButton.setEnabled(false);
        dataBinding.addButton.setEnabled(false);
        dataBinding.textInputLayout.setEnabled(false);
        dataBinding.textInputLayout2.setEnabled(false);
        dataBinding.newChatroomIv.setImageAlpha(125);
    }

}
