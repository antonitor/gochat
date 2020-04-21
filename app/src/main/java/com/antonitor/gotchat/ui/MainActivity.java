package com.antonitor.gotchat.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.antonitor.gotchat.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add viwemodel

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
          FirebaseUser user = firebaseAuth.getCurrentUser();
          if (user != null) {
              onSingedInInitialize(user);
          } else {
              onSingedOutCleanup();
              List<AuthUI.IdpConfig> providers =
                      Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

              startActivityForResult(AuthUI.getInstance()
                              .createSignInIntentBuilder()
                              .setLogo(R.mipmap.ic_launcher)
                              .setTheme(R.style.AppTheme)
                              .setAvailableProviders(providers)
                              .build()
                      , RC_SIGN_IN);
          }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Hello " + mUser.getPhoneNumber() + " !!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSingedInInitialize(FirebaseUser user) {
        mUser = user;
    }

    private void onSingedOutCleanup() {
        mUser = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener !=null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
