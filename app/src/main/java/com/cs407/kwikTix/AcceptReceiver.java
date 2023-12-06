package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AcceptReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(context.getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        String acceptedOfferId = intent.getStringExtra("offerId");
        String listingId = intent.getStringExtra("listingId");
        Log.d("acceptedOfferId", acceptedOfferId);
        Log.d("listingId", listingId);


        Toast.makeText(context, context.getString(R.string.ACCEPTED),
                Toast.LENGTH_LONG).show();

        // TODO change status of offer to accepted
        //NotificationHelper.getInstance().showNotification(context, -1);
    }
}
