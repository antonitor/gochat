package com.antonitor.gotchat.sync;

import android.graphics.Bitmap;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;


public class FirebaseStorageRepository {

    private static final String LOG_TAG = FirebaseDatabaseRepository.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static FirebaseStorageRepository sInstance;
    private final StorageReference roomImageStorageReference;
    private final StorageReference msgImageStorageReference;


    private FirebaseStorageRepository(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        roomImageStorageReference = firebaseStorage.getReference(FirebaseContract.ROOM_IMAGE_REF);
        msgImageStorageReference = firebaseStorage.getReference(FirebaseContract.MESSAGE_IMAGE_REF);
    }

    public static FirebaseStorageRepository getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseStorageRepository();
            }
        }
        return sInstance;
    }

    public StorageReference getRoomImageStorageReference() {
        return roomImageStorageReference;
    }
    public StorageReference getMsgImageStorageReference() { return msgImageStorageReference; }

    public UploadTask uploadBitmap(Bitmap bitmap, StorageReference reference) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final String randomName = "image-"+ (new Date().getTime());
        final StorageReference pictureRef = reference.child(randomName +".jpg");
        return pictureRef.putBytes(baos.toByteArray());
    }

    public UploadTask uploadFromLocal(Uri localUri) {
        StorageReference photoRef = roomImageStorageReference.child(localUri.getLastPathSegment());
        return photoRef.putFile(localUri);
    }


}
