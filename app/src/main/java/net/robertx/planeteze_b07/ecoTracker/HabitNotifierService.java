package net.robertx.planeteze_b07.ecoTracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.Dashboard;

public class HabitNotifierService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "habit_notifications_channel";

    @Override
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Create notification channel if necessary
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Habit Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        // Create an intent to open the Dashboard activity
        Intent intent = new Intent(this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());

        Intent broadcastIntent = new Intent("net.robertx.planeteze_b07.FCM_MESSAGE");
        broadcastIntent.putExtra("message", remoteMessage.getNotification().getTitle() + ": " + remoteMessage.getNotification().getBody());
        sendBroadcast(broadcastIntent);

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("HabitNotifierService", "Refreshed token: " + token);
        // send token to server
    }
}