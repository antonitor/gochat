package com.antonitor.gotchat.ui.newroom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.antonitor.gotchat.utilities.Utilities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewRoomViewModel extends ViewModel {

    private static final String TAG = "ADD_NEW_ROOM_VIEW_MODEL";

    private boolean imageChosen;
    private Uri localImageUri;
    private Bitmap bitmap;
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private final MutableLiveData<Double> uploadProgress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    void newChatRoom(String title, String topic, String url){
        FirebaseDatabaseRepository.getInstance().newChatRoom(title, topic, url);
    }

    void uploadImage() {
        Bitmap thumbnail = Utilities.getThumbnail(bitmap);
        FirebaseStorageRepository storageRepository = FirebaseStorageRepository.getInstance();
        storageRepository.uploadBitmap(thumbnail, storageRepository.getRoomImgRef(),
                new FirebaseStorageRepository.UploadCallback() {
                    @Override
                    public void onComplete(String downloadUrl) {
                        imageUrl.postValue(downloadUrl);
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onProgress(Double progress) {
                        isLoading.postValue(true);
                        uploadProgress.postValue(progress);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        imageUrl.postValue(null);
                        Log.d(TAG, e.getMessage());
                    }
                });
    }

    MutableLiveData<String> getImageUrl() {
        return imageUrl;
    }

    MutableLiveData<Double> getUploadProgress() {
        return uploadProgress;
    }

    MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    boolean isImageChosen() {
        return imageChosen;
    }

    void setImageChosen() {
        this.imageChosen = true;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Uri getLocalImageUri() {
        return localImageUri;
    }

    void setLocalImageUri(Uri localImageUri) {
        this.localImageUri = localImageUri;
    }

}
