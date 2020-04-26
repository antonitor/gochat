package com.antonitor.gotchat.ui.roomlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.sync.FirebaseGoChatData;
import com.antonitor.gotchat.sync.Repository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = MainActivity.class.getCanonicalName();

    private Repository mRepository;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize repository
        mRepository = Repository.getInstance();

        //add viwemodel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

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
                FirebaseGoChatData.getInstance().setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
                Log.d(TAG, "LOGGED AS " + mRepository.getmUser().getPhoneNumber());
            } else {
                Log.e(TAG, "SIGN_IN FAILED");
                finish();
            }
        }
    }


    private void onSingedInInitialize(FirebaseUser user) {

        FirebaseGoChatData.getInstance().setFirebaseUser(user);
        Log.d(TAG, "LOGGED AS " + FirebaseGoChatData.getInstance().getFirebaseUser().getPhoneNumber());
        startFragmentPageAdapter();

    }

    private void onSingedOutCleanup() {
        mRepository.setmUser(null);
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
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register AuthStateListener
        if (mAuthStateListener !=null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
