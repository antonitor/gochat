package com.antonitor.gotchat.sync;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.antonitor.gotchat.utilities.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import androidx.annotation.NonNull;


public class FirebaseStorageRepository {

    private static final String LOG_TAG = "FB_STORAGE_REPO";
    private static final Object LOCK = new Object();

    private static FirebaseStorageRepository sInstance;
    private final StorageReference roomImgRef;
    private final StorageReference msgImgRef;
    private final StorageReference userImgRef;

    public interface UploadCallback {
        void onComplete(String downloadUrl);

        void onProgress(Double progress);

        void onFailure(Exception e);
    }


    private FirebaseStorageRepository() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        roomImgRef = firebaseStorage.getReference(FirebaseContract.ROOM_IMAGE_REF);
        msgImgRef = firebaseStorage.getReference(FirebaseContract.MESSAGE_IMAGE_REF);
        userImgRef = firebaseStorage.getReference(FirebaseContract.USER_IMAGE_REF);
    }

    public static FirebaseStorageRepository getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseStorageRepository();
            }
        }
        return sInstance;
    }

    public StorageReference getRoomImgRef() {
        return roomImgRef;
    }

    public StorageReference getMsgImgRef() {
        return msgImgRef;
    }

    public StorageReference getUserImgRef() {
        return userImgRef;
    }

    public void uploadBitmap(Bitmap bitmap, StorageReference reference, UploadCallback callback) {
        final String randomName = "image-" + (new Date().getTime());
        final StorageReference pictureRef = reference.child(randomName + ".jpg");
        UploadTask uploadTask = pictureRef.putBytes(Utilities.bitmapByteArray(bitmap));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                callback.onComplete(downloadUrl.toString());
                Log.d(LOG_TAG, "SUCCESSFUL BITMAP UPLOAD");
                Log.d(LOG_TAG, "File: " + taskSnapshot.getMetadata().getName());
                Log.d(LOG_TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                Log.d(LOG_TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes() / 1000 + " kb");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                callback.onProgress(progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void uploadFromLocal(Uri localUri, StorageReference reference, UploadCallback callback) {
        StorageReference photoRef = reference.child(localUri.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(localUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                callback.onComplete(downloadUrl.toString());
                Log.d(LOG_TAG, "SUCCESSFUL BITMAP UPLOAD");
                Log.d(LOG_TAG, "File: " + taskSnapshot.getMetadata().getName());
                Log.d(LOG_TAG, "Path: " + taskSnapshot.getMetadata().getPath());
                Log.d(LOG_TAG, "Size: " + taskSnapshot.getMetadata().getSizeBytes() / 1000 + " kb");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                callback.onProgress(progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }


}
