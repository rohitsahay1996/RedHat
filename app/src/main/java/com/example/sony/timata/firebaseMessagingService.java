package com.example.sony.timata;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Rohit on 9/16/2017.
 */

public class firebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).
                setContentTitle("Friend Request").
                setContentText("You have received a new friend request");

        // set an ID for notification
        int mNotificationID = (int) System.currentTimeMillis();
        //get an instance of an Notification manager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Build the notification that issues it
        mNotifyMgr.notify(mNotificationID, mBuilder.build());

    }
}
