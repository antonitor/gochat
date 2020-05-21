package com.antonitor.gotchat.ui.image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.ActivityImageBinding;
import com.antonitor.gotchat.model.Message;
import com.bumptech.glide.Glide;


public class ImageActivity extends AppCompatActivity {

    ActivityImageBinding dataBinding;
    Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        //setTitle("");
        message = getIntent().getExtras().getParcelable(getString(R.string.extra_image_message));

        Glide.with(this)
                .load(message.getPhotoUrl())
                .into(dataBinding.imageFullsize);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.share_image:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(message.getLocalPhotoUrl()));
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
