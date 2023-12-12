package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;

public class AcceptReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(context.getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        int notificationId = intent.getIntExtra("notificationId", -1);
        String acceptedOfferId = intent.getStringExtra("offerId");
        String listingId = intent.getStringExtra("listingId");
        String buyerUsername = intent.getStringExtra("buyerUsername");

        ArrayList<Offer> offers = dbHelper.getOffers(buyerUsername, acceptedOfferId);
        Offer offer;
        Log.d("AcceptReceiver", "onReceive");
        Log.d("AcceptReceiver", "acceptedOfferId: " + acceptedOfferId);
        if (offers.size() != 0) {
            Log.d("AcceptReceiver", "offers.size(): " + Integer.toString(offers.size()));
//            offer = offers.get(0);
            offer = offers.get(Integer.parseInt(acceptedOfferId) - 1);
            dbHelper.acceptOffer(offer);
            for (Offer thing : offers) {
                Log.d("Offer Info", thing.getBuyerUsername() + ", " + thing.getOfferAmount() + ", " + thing.getId());
            }

            // Cancel notification
            notificationManagerCompat.cancel(notificationId);
        }

        Toast.makeText(context, context.getString(R.string.ACCEPTED),
                Toast.LENGTH_LONG).show();
    }
}
