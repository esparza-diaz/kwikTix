package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RejectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(context.getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        String rejectedOfferId = intent.getStringExtra("offerId");
        String listingId = intent.getStringExtra("listingId");
        Log.d("rejectedOfferId", rejectedOfferId);
        Log.d("listingId", listingId);

        Toast.makeText(context, context.getString(R.string.REJECTED),
                Toast.LENGTH_LONG).show();

        // TODO update rejected offer status
//        NotificationHelper.getInstance().showNotification(context, rejectId);
    }
}
