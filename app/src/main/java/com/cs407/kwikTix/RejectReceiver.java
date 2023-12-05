package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RejectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int rejectId = intent.getIntExtra("id", -1);

        if (rejectId != -1) {
            Log.d("reject", "not -1");
        }

        Toast.makeText(context, context.getString(R.string.REJECTED),
                Toast.LENGTH_LONG).show();
        NotificationHelper.getInstance().showNotification(context, rejectId);
    }
}
