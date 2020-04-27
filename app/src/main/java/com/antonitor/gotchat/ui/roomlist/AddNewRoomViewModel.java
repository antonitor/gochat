package com.antonitor.gotchat.ui.roomlist;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewRoomViewModel extends ViewModel {

    private boolean imageChosen;
    private boolean isBitmap;
    private Uri uri;
    private Bitmap bitmap;
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isBitmap() {
        return isBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        isBitmap = true;
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        isBitmap = false;
        this.uri = uri;
    }

    public boolean isImageChosen() {
        return imageChosen;
    }

    public void setImageChosen(boolean imageChosen) {
        this.imageChosen = imageChosen;
    }

    public MutableLiveData<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.setValue(imageUrl);
    }


}
