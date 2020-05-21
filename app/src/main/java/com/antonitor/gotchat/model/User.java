package com.antonitor.gotchat.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.antonitor.gotchat.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

import androidx.databinding.BindingAdapter;

public class User implements Parcelable {

    private String UUID;
    private String telephoneNumber;
    private String userName;
    private String userEmail;
    private String cloudPhotoUrl;
    private String localPhotoUrl;
    private String status;
    private HashMap<String, ChatRoom> ownChatRooms = new HashMap<>();
    private HashMap<String, ChatRoom> subscribedChatRooms = new HashMap<>();

    public User() {
    }

    public User(String UUID, String email, String userName) {
        this.UUID = UUID;
        this.userEmail = email;
        this.userName = userName;
    }

    protected User(Parcel in) {
        UUID = in.readString();
        telephoneNumber = in.readString();
        userName = in.readString();
        cloudPhotoUrl = in.readString();
        localPhotoUrl = in.readString();
        userEmail = in.readString();
        status = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCloudPhotoUrl() {
        return cloudPhotoUrl;
    }

    public void setCloudPhotoUrl(String cloudPhotoUrl) {
        this.cloudPhotoUrl = cloudPhotoUrl;
    }

    public String getLocalPhotoUrl() {
        return localPhotoUrl;
    }

    public void setLocalPhotoUrl(String localPhotoUrl) {
        this.localPhotoUrl = localPhotoUrl;
    }

    public HashMap<String, ChatRoom> getOwnChatRooms() {
        return ownChatRooms;
    }

    public void setOwnChatRooms(HashMap<String, ChatRoom> ownChatRooms) {
        this.ownChatRooms = ownChatRooms;
    }

    public HashMap<String, ChatRoom> getSubscribedChatRooms() {
        return subscribedChatRooms;
    }

    public void setSubscribedChatRooms(HashMap<String, ChatRoom> subscribedChatRooms) {
        this.subscribedChatRooms = subscribedChatRooms;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UUID);
        parcel.writeString(telephoneNumber);
        parcel.writeString(userName);
        parcel.writeString(cloudPhotoUrl);
        parcel.writeString(localPhotoUrl);
        parcel.writeString(userEmail);
        parcel.writeString(status);
    }

    @BindingAdapter("avatar")
    public static void loadImage(ImageView view, String cloudPhotoUrl) {
        Glide.with(view.getContext())
                .load(cloudPhotoUrl).apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.noimage)
                .into(view);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
