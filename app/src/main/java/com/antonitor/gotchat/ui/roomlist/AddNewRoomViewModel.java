package com.antonitor.gotchat.ui.roomlist;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewRoomViewModel extends ViewModel {

    private static final String TAG = "ADD_NEW_ROOM_VIEWMODEL";

    private boolean imageChosen;
    private Uri localImageUri;
    private Bitmap bitmap;
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private MutableLiveData<Double> uploadProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    void newChatRoom(String title, String topic, String url){
        FirebaseDatabaseRepository.getInstance().newChatRoom(title, title, topic, url);

    }

    void uploadImage() {
        if (bitmap != null) {
            UploadTask upTask = FirebaseStorageRepository.getInstance().uploadBitmap(bitmap);
            upTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "SUCCESFULL BITMAP UPLOAD");
                Log.d(TAG, "File: " + taskSnapshot.getMetadata().getName());
                Log.d(TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                Log.d(TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes()/1000 + " kb");
                Log.d(TAG, "Encoding: " + taskSnapshot.getMetadata().getContentEncoding());
                Log.d(TAG, "Upload time: " + taskSnapshot.getMetadata().getUpdatedTimeMillis()/1000 + " sec.");
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                imageUrl.postValue(downloadUrl.toString());
                isLoading.postValue(false);
            }).addOnProgressListener(taskSnapshot -> {
                isLoading.postValue(true);
                uploadProgress.postValue(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            }).addOnFailureListener(e -> {
                imageUrl.postValue(null);
                Log.d(TAG, "FAILED BITMAP UPLOAD");
                Log.d(TAG, e.getMessage());
            });
        } else if (localImageUri!=null) {
            UploadTask upTask = FirebaseStorageRepository.getInstance().uploadFromLocal(localImageUri);
            upTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "SUCCESFULL BITMAP UPLOAD");
                Log.d(TAG, "File: " + taskSnapshot.getMetadata().getName());
                Log.d(TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                Log.d(TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes()/1000 + " kb");
                Log.d(TAG, "Encoding: " + taskSnapshot.getMetadata().getContentEncoding());
                Log.d(TAG, "Upload time: " + taskSnapshot.getMetadata().getUpdatedTimeMillis()/1000 + " sec.");
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                imageUrl.postValue(downloadUrl.toString());
                isLoading.postValue(false);
            }).addOnFailureListener(e -> {
                imageUrl.postValue(null);
                Log.d(TAG, "FAILED BITMAP UPLOAD");
                Log.d(TAG, e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                isLoading.postValue(true);
                uploadProgress.postValue(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            });
        } else {
            Log.d(TAG, "USER HAVEN'T SELECTED ANY IMAGE");
            imageUrl.postValue(null);
        }
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
