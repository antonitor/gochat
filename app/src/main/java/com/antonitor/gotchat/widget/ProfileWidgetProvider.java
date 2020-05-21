package com.antonitor.gotchat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.model.User;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.ui.profile.ProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Implementation of App Widget functionality.
 */
public class ProfileWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.profile_widget_provider);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String UUID = auth.getCurrentUser().getUid();
        FirebaseDatabaseRepository.getInstance().getUser(UUID, new FirebaseDatabaseRepository.GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    Log.d("WIDGET", "-------WIDGED -------------------------------->>>>" + user.getUUID());
                    views.setTextViewText(R.id.appwidget_username, user.getUserName());
                    views.setTextViewText(R.id.appwidget_phone_number, user.getTelephoneNumber());
                    views.setTextViewText(R.id.appwidget_status, context.getString(R.string.widget_status) +  user.getStatus());
                    AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide
                            .with(context.getApplicationContext())
                            .asBitmap()
                            .load(user.getCloudPhotoUrl())
                            .centerCrop()
                            .into(appWidgetTarget);
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra(context.getResources().getString(R.string.extra_user_uuid), user.getUUID());
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

