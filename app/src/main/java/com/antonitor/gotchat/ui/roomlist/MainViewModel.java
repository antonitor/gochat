package com.antonitor.gotchat.ui.roomlist;

import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private FirebaseUser mUser;

    public MainViewModel() {

    }

    public FirebaseUser getmUser() {
        return mUser;
    }

    public void setmUser(FirebaseUser mUser) {
        this.mUser = mUser;
    }
}
