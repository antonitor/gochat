package com.antonitor.gotchat.ui.roomlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.ui.newroom.AddNewRoomActivity;
import com.antonitor.gotchat.utilities.Utilities;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private static final int PERMISSIONS_REQ = 123;
    private static final String[] STORAGE_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    PagerAdapter pagerAdapter;
    FloatingActionButton fab;
    private MainViewModel mainViewModel;
    private Observer<Boolean> loginObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean login) {
            if (login) {
                Log.d(LOG_TAG, "----------- OBSERVING LOGIN: CHANGED TRUE -------");
                Log.d(LOG_TAG, "---------------- REQUEST PERMISSIONS ------------");
                requestPermissions();
            } else {
                Log.d(LOG_TAG, "----------- OBSERVING LOGIN: CHANGED FALSE -------");
                Log.d(LOG_TAG, "---------------- START LOGIN SCREEN --------------");
                startLoginScreen();
            }
        }
    };

    //TODO: Add fab to MainActivity layout instead of fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.add_fab);
        EmojiManager.install(new GoogleEmojiProvider());

        //Setup ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getLogin().observe(this, loginObserver);
        mainViewModel.startAuthenticationListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }



    public void requestPermissions(){
        if (Utilities.hasPermissions(this, STORAGE_PERMISSIONS)) {
            startFragmentPageAdapter();
        } else {
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, PERMISSIONS_REQ);
        }
    }

    private void startLoginScreen() {
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());

        startActivity(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(providers)
                .build());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "---------------- SHOWING FRAGMENT ------------");
                    startFragmentPageAdapter();
                } else {
                    Log.d(LOG_TAG, "---------------- REQUEST PERMISSIONS AGAIN!!!----------");
                    requestPermissions();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void startFragmentPageAdapter() {
        ViewPager viewPager = findViewById(R.id.pager);
        pagerAdapter = new MainPageAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        fab.setOnClickListener(view -> {

                        });
                        fab.setImageResource(R.drawable.ic_search_black_24dp);
                        fab.show();
                        break;
                    case 1:
                        fab.hide();
                        break;
                    case 2:
                        fab.setOnClickListener(view -> startActivity(new Intent(
                                MainActivity.this, AddNewRoomActivity.class)));
                        fab.setImageResource(R.drawable.ic_add_black_24dp);
                        fab.show();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_singout)
            mainViewModel.singOut();
        return super.onOptionsItemSelected(item);
    }
}

