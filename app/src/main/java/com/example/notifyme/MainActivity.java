package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button notify_button;
    private Button cancel_button;
    private Button update_button;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private static final String ACTION_UPDATE_NOTIFICATION =
            BuildConfig.APPLICATION_ID + ".ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION =
            BuildConfig.APPLICATION_ID + ".ACTION_CANCEL_NOTIFICATION";

    private NotificationReceiver mReceiver = new NotificationReceiver();

    // Variable to store NotificationManager object
    private NotificationManager mNotifyManager;

    // Creating constant for the notification ID
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notify_button = findViewById(R.id.notify);
        notify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });
        
        cancel_button =findViewById(R.id.cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelNotification();
            }
        });
        
        update_button = findViewById(R.id.update);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        createNotificationChannel();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION);

        registerReceiver(mReceiver, intentFilter);

        // registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        // registerReceiver(mReceiver, new IntentFilter(ACTION_CANCEL_NOTIFICATION));

        setNotificationButtonState(true, false, false);
    }

    private void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.mascot_1);

        // Get Notification Builder
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setStyle(new NotificationCompat.InboxStyle()
                .addLine("Here goes line 1")
                .addLine("Line 2")
                .setSummaryText("This is a summary text!")
                .setBigContentTitle("Notification Updated!"));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    private void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true,false, false);
    }

    public void sendNotification() {
        //Creating the new intent for adding update action in the notifiation
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);

        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        // Get the notification Builder
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification!", updatePendingIntent);

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create notifiacation channel if android version 26 or higher is present
        // Because it is must to have atleast one notification channel for the app
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "MASCOTT NOTIFICATION", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("New Mascot Notifiation");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        // Create explicit intent method to launch the activity
        Intent notificationIntent = new Intent(this, MainActivity.class);

        // Create the intent for delete notification from the notification bar itself
        Intent deleteNotificationIntent = new Intent(ACTION_CANCEL_NOTIFICATION);

        // Creating the PendingIntent
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deleteNotificationPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, deleteNotificationIntent, PendingIntent.FLAG_ONE_SHOT);

        // Building the notification
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
                this, PRIMARY_CHANNEL_ID);
        notifyBuilder.setContentTitle("You have been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDeleteIntent(deleteNotificationPendingIntent);

        return notifyBuilder;
    }

    void setNotificationButtonState (boolean isNotifyEnabled,
                                     boolean isUpdateEnabled,
                                     boolean isCancelEnabled) {
        notify_button.setEnabled(isNotifyEnabled);
        update_button.setEnabled(isUpdateEnabled);
        cancel_button.setEnabled(isCancelEnabled);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action != null) {
                Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
                switch (action) {
                    case ACTION_UPDATE_NOTIFICATION:
                        updateNotification();
                        break;
                    case ACTION_CANCEL_NOTIFICATION:
                        setNotificationButtonState(true, false, false);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}