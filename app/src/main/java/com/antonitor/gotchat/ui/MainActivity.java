package com.antonitor.gotchat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.antonitor.gotchat.R;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
