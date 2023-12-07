package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

public class StandardNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        int notificationId = intent.getIntExtra("notificationId", -1);

        // Cancel notification
        notificationManagerCompat.cancel(notificationId);
    }
}
