package com.antonitor.gotchat.ui.chatroom;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.model.Message;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseStorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "CHAT_VIEW_MODEL";

    private ChatRoom chatRoom;
    private boolean imageChosen;
    private Uri localImageUri;
    private Bitmap bitmap;
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private MutableLiveData<Double> uploadProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    void postMessage(Message message) {
        FirebaseDatabaseRepository.getInstance().postMessage(message);
    }

    void uploadImage(StorageReference reference) {
        if (bitmap != null) {
            UploadTask upTask = FirebaseStorageRepository.getInstance().uploadBitmap(bitmap, reference);
            upTask.addOnSuccessListener(taskSnapshot -> {
                Log.v(TAG, "SUCCESFULL BITMAP UPLOAD");
                Log.v(TAG, "File: " + taskSnapshot.getMetadata().getName());
                Log.v(TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                Log.v(TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes()/1000 + " kb");
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

    ChatRoom getChatRoom() {
        return chatRoom;
    }

    void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
