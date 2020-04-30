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
    private static final String ROOM_IMAGE = "chatroom_images";
    private static final String MESSAGE_IMAGE = "message_images";
    private static FirebaseStorageRepository sInstance;
    private FirebaseStorage firebaseStorage;
    private StorageReference roomImageStorageReference;
    private StorageReference msgImageStorageReference;


    private FirebaseStorageRepository(){
        firebaseStorage = FirebaseStorage.getInstance();
        roomImageStorageReference = firebaseStorage.getReference(ROOM_IMAGE);
        msgImageStorageReference = firebaseStorage.getReference(MESSAGE_IMAGE);
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

    public UploadTask uploadBitmap(Bitmap bitmap, StorageReference refernce) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final String randomName = "image-"+ (new Date().getTime());
        final StorageReference pictureRef = refernce.child(randomName +".jpg");
        return pictureRef.putBytes(baos.toByteArray());
    }

    public UploadTask uploadFromLocal(Uri localUri) {
        StorageReference photoRef = roomImageStorageReference.child(localUri.getLastPathSegment());
        return photoRef.putFile(localUri);
    }


}
