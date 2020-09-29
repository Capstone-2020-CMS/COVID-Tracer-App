package com.covid.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.covid.R;

public class NoteManager {
    public static final String CHANNEL_1_ID = "channel 1";
    public static final String CHANNEL_2_ID = "channel 2";




    public NoteManager(Context context){

    }


    public static void createNotificationChannels(Context context){
        // Check if SDK version is 26 or higher
        // notification channels only work on 26 or higher

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Create high priority channel
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "High Priority Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Hi-P Channel");

            // Create low priority channel
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Low Priority Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Low-P Channel");

            NotificationManager manager = context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }


    }


    public Notification createHighPriorityNote(String contents, Context context){
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_emptybubble)
                .setContentTitle("ALART!")
                .setContentText("Big Alart Please CODE: Expono")
                // Set priority is used for API lower than 26/Oreo works like Channel system
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set category to be used to control behaviour https://developer.android.com/reference/android/app/Notification.html
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        return notification;

        //notificationManager.notify(1, notification);

    }
    public Notification createLowPriorityNote(String contents, Context context){
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_emptybubble)
                .setContentTitle("alart?!")
                .setContentText("small alart Please CODE: No expono")
                // Set priority is used for API lower than 26/Oreo works like Channel system
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Set category to be used to control behaviour https://developer.android.com/reference/android/app/Notification.html
                //.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        return notification;

        //notificationManager.notify(2, notification);

    }
}
