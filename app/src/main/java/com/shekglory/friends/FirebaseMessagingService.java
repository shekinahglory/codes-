package com.shekglory.friends;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    public static final String CHANNEL_ID = "friendRequestIdCleanChat";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);



        int mNotificationId = (int) System.currentTimeMillis();

        String notification_tile = remoteMessage.getNotification().getTitle();

        if (notification_tile.equals("Friend Request")){
            String notification_message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();




            String from_user_id = remoteMessage.getData().get("from_user_id");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.iconap)
                    .setContentTitle(notification_tile)
                    .setContentText(notification_message)
                    ;


            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_id", from_user_id);
            PendingIntent resultPendinIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendinIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

        } else {

            String notification_message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            String from_user_id = remoteMessage.getData().get("message_user_id");
            String user_name = remoteMessage.getData().get("user_name");
            String user_image = remoteMessage.getNotification().getIcon();


            NotificationCompat.Builder mBuilder = null
                    ;
            try {
                mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.iconap)
                        .setLargeIcon(Picasso.with(getApplicationContext()).load(user_image).get())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(Picasso.with(getApplicationContext()).load(user_image).get())
                        .bigLargeIcon(null))
                        .setContentTitle(notification_tile)
                        .setContentText(notification_message);

            } catch (IOException e) {
                e.printStackTrace();
            }


            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_id", from_user_id);
            resultIntent.putExtra("user_name", user_name);
            PendingIntent resultPendinIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendinIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.notify(mNotificationId, mBuilder.build());




        }


    }



}
