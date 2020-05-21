package com.antonitor.gotchat.ui.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.antonitor.gotchat.utilities.Utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private static final String LOG_TAG = "PROFILE_VIEWMODEL";
    private MutableLiveData<User> user= new MutableLiveData<>();
    private String userUUID;
    private final MutableLiveData<Double> uploadProgress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.postValue(user);
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }


    public void fetchUser(String userUUID){
        FirebaseDatabaseRepository.getInstance().getUser(userUUID, new FirebaseDatabaseRepository.GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                setUser(user);
            }

            @Override
            public void onError(Exception e) {
                setUser(null);
            }
        });
    }

    public void updateUser(User user){
        FirebaseDatabaseRepository.getInstance().updateUser(user);
        if (user.getUUID() == FirebaseAuthRepository.getInstance().getCustomUser().getUUID()) {
            FirebaseAuthRepository.getInstance().getCustomUser().setUserName(user.getUserName());
            FirebaseAuthRepository.getInstance().getCustomUser().setTelephoneNumber(user.getTelephoneNumber());
            FirebaseAuthRepository.getInstance().getCustomUser().setStatus(user.getStatus());
        }
    }

    void uploadImage(Bitmap bitmap) {
        Bitmap thumbnail = Utilities.getThumbnail(bitmap);
        FirebaseStorageRepository storageRepository = FirebaseStorageRepository.getInstance();
        storageRepository.uploadBitmap(thumbnail, storageRepository.getUserImgRef(),
                new FirebaseStorageRepository.UploadCallback() {
                    @Override
                    public void onComplete(String downloadUrl) {
                        User u = user.getValue();
                        u.setCloudPhotoUrl(downloadUrl);
                        user.postValue(u);
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onProgress(Double progress) {
                        isLoading.postValue(true);
                        uploadProgress.postValue(progress);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        User u = user.getValue();
                        u.setCloudPhotoUrl(null);
                        user.postValue(u);
                        isLoading.postValue(false);
                    }
                });
    }

    public MutableLiveData<Double> getUploadProgress() {
        return uploadProgress;
    }


    public Uri getLocalImageUri() {
        return Uri.parse(user.getValue().getLocalPhotoUrl());
    }

    public void setLocalImageUri(Uri localImageUri) {
        User u = user.getValue();
        u.setLocalPhotoUrl(localImageUri.toString());
        user.postValue(u);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
