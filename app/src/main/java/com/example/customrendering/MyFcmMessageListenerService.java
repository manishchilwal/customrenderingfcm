package com.example.customrendering;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFcmMessageListenerService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "manishTest";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESC = "Channel Description";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        try {
            if (message.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Log.d("FCM Message", "Key: " + key + ", Value: " + value);
                    extras.putString(key, value);
                }
                for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

                // Check if the message is from CleverTap
                if (CleverTapAPI.getNotificationInfo(extras).fromCleverTap && (extras.getString("nt_title") == null) && extras.getString("nt_message") == null) {
                    // Fetching data from CleverTap payload
                    String ct_title = extras.getString("nt");
                    String ct_message = extras.getString("nm");

                    // Build notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(ct_title)
                            .setContentText(ct_message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    // Create notification channel (required for Android Oreo and above)
                    createNotificationChannel();

                    // Issue the notification
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());
                } else if (CleverTapAPI.getNotificationInfo(extras).fromCleverTap) {
                    String ct_title = extras.getString("nt_title");
                    String ct_message = extras.getString("nt_message");
                    Integer ct_id = Integer.parseInt(Objects.requireNonNull(extras.getString("nt_id")));
                    String ct_img = extras.getString("nt_image");

                    // Build notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(ct_title)
                            .setAutoCancel(true)
                            .setContentText(ct_message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    // Create notification channel (required for Android Oreo and above)
                    createNotificationChannel();

                    // Set up BigPictureStyle for displaying image in the notification
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                    Glide.with(this)
                            .asBitmap()
                            .load(ct_img)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    bigPictureStyle.bigPicture(resource).build();
                                    // Set the BigPictureStyle on the builder
                                    builder.setStyle(bigPictureStyle);

                                    // Issue the notification
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyFcmMessageListenerService.this);
                                    if (ActivityCompat.checkSelfPermission(MyFcmMessageListenerService.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    notificationManager.notify(ct_id, builder.build());
                                }
                            });
                }
            }
        } catch (Throwable t) {
            Log.e("MyFcmMessageListener", "Error parsing FCM message", t);
        }
    }

    private void createNotificationChannel() {
        // Check if the device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

