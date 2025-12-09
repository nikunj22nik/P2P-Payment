package com.p2p.application.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.p2p.application.R;
import com.p2p.application.activity.MainActivity;
import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("dkp", "From: " + remoteMessage.getFrom());
        Log.d("dkp", "From: " + remoteMessage.getFrom());
        Log.v("dkp", "From: " + remoteMessage.getData());
        showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),remoteMessage.getNotification().getBody());
    }
    // Method to display the notifications
    public void showNotification(String title, String message) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            String channel_id = "com.p2p.application";
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
                    .setSmallIcon(R.mipmap.app_icon_round)
                    .setContentIntent(pendingIntent);
                builder = builder.setContentTitle(title).setContentText(message);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(0, builder.build());

        } catch (Exception e) {
            Log.v("ex", Objects.requireNonNull(e.getMessage()));
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Log.d("dkp", "fcm_token: " + token);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("fcmToken", token).apply();
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

    }

}
