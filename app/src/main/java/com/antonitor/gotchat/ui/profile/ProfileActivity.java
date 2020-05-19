package com.antonitor.gotchat.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityProfileBinding;
import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

import static com.antonitor.gotchat.utilities.Utilities.bitmapByteArray;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_CAMERA_ACTION = 123;
    private static final int RC_PHOTO_PICKER = 456;
    ActivityProfileBinding dataBinding;
    ProfileViewModel viewModel;
    private Observer<Boolean> loadingObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isLoading) {
            if(isLoading) {
                Observer<Double> progress = aDouble -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dataBinding.progressBarAvatarUpload.setProgress(aDouble.intValue(), true);
                    }
                };
                viewModel.getUploadProgress().observe(ProfileActivity.this, progress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.setUserUUID(getIntent().getStringExtra(getString(R.string.extra_user_uuid)));
        if (viewModel.getUserUUID().equals(FirebaseAuthRepository.getInstance().getCustomUser().getUUID())) {
            viewModel.getIsLoading().observe(this, loadingObserver);
        } else {
            dataBinding.addPictureButton.setVisibility(View.GONE);
            dataBinding.takePictureButton.setVisibility(View.GONE);
            dataBinding.applyProfileChanges.setVisibility(View.GONE);
            dataBinding.userNameEt.setEnabled(false);
            dataBinding.statusEt.setEnabled(false);
        }
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null)
                    dataBinding.setUser(user);
                else
                    Toast.makeText(ProfileActivity.this, "User not found",
                            Toast.LENGTH_LONG).show();
            }
        });
        viewModel.fetchtUser(viewModel.getUserUUID());
    }

    public void okClicked(View view) {
        User u = viewModel.getUser().getValue();
        u.setUserName(dataBinding.userNameEt.getText().toString());
        u.setTelephoneNumber(dataBinding.phoneNumbeerEt.getText().toString());
        u.setStatus(dataBinding.statusEt.getText().toString());
        viewModel.updateUser(u);
        finish();
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
                    Uri localImageUri = data.getData();
                    viewModel.setLocalImageUri(localImageUri);
                    try {
                        viewModel.uploadImage(MediaStore.Images.Media.getBitmap(getContentResolver()
                                , localImageUri));
                        Glide.with(this)
                                .load(localImageUri)
                                .into(dataBinding.profilePricture);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RC_CAMERA_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if(data.getExtras()==null || !data.getExtras().containsKey("data")) return;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    viewModel.uploadImage(imageBitmap);
                    Glide.with(this)
                            .asBitmap()
                            .load(bitmapByteArray(imageBitmap))
                            .apply(new RequestOptions().circleCrop())
                            .into(dataBinding.profilePricture);
                }
        }
    }
}
