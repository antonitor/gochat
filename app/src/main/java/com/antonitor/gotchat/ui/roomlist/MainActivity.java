package com.antonitor.gotchat.ui.roomlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.utilities.Utilities;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 1341;
    public static final int PERMISSIONS_REQUEST = 123;
    public static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final String TAG = MainActivity.class.getCanonicalName();

    private FirebaseDatabaseRepository databaseRepository;
    private FirebaseAuthRepository firebaseAuthRepository;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EmojiManager.install(new GoogleEmojiProvider());
        setAuthListener();
        //add viwemodel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //Initialize repositories
        databaseRepository = FirebaseDatabaseRepository.getInstance();
        firebaseAuthRepository = FirebaseAuthRepository.getInstance();

        if (!Utilities.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setAuthListener() {
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
                firebaseAuthRepository.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
                Log.d(TAG, "LOGGED AS " + firebaseAuthRepository.getFirebaseUser().getPhoneNumber());
            } else {
                Log.e(TAG, "SIGN_IN FAILED");
                finish();
            }
        }
    }



    private void onSingedInInitialize(FirebaseUser user) {
        firebaseAuthRepository.setFirebaseUser(user);
        Log.d(TAG, "LOGGED AS " + firebaseAuthRepository.getFirebaseUser().getPhoneNumber());
        startFragmentPageAdapter();
    }

    private void onSingedOutCleanup() {
        firebaseAuthRepository.setFirebaseUser(null);
    }

    private void startFragmentPageAdapter() {
        ViewPager viewPager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new MainPageAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //register AuthStateListener
        FirebaseAuthRepository.getInstance().getFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register AuthStateListener
        if (mAuthStateListener !=null)
            FirebaseAuthRepository.getInstance().getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
    }

}

