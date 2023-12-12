package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;

public class RejectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(context.getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        int notificationId = intent.getIntExtra("notificationId", -1);
        String rejectedOfferId = intent.getStringExtra("offerId");
        String listingId = intent.getStringExtra("listingId");
        String buyerUsername = intent.getStringExtra("buyerUsername");

        Log.d("RejectReceiver", "onReceive");

        ArrayList<Offer> offers = dbHelper.getOffers(buyerUsername, rejectedOfferId);
        Offer offer;
        if (offers.size() != 0) {
            offer = offers.get(Integer.parseInt(rejectedOfferId) - 1);
            dbHelper.declineOffer(offer);
        }

        Toast.makeText(context, context.getString(R.string.REJECTED),
                Toast.LENGTH_LONG).show();

        // Cancel notification
        notificationManagerCompat.cancel(notificationId);
    }
}
