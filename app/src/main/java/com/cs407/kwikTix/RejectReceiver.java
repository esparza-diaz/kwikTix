package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RejectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(context.getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        String rejectedOfferId = intent.getStringExtra("offerId");
        String listingId = intent.getStringExtra("listingId");
        String buyerUsername = intent.getStringExtra("buyerUsername");
        Log.d("rejectedOfferId", rejectedOfferId);
        Log.d("listingId", listingId);
        Log.d("buyerUsername", buyerUsername);


        ArrayList<Offer> offers = dbHelper.getOffers(buyerUsername, listingId);
        Offer offer;
        if (offers.size() != 0) {
            offer = offers.get(0);
            Log.d("Reject Offer Info", offer.getBuyerUsername());
            dbHelper.declineOffer(offer);
        } else {
            Log.d("Reject Offer Info", "didn't find offer");

        }

        Toast.makeText(context, context.getString(R.string.REJECTED),
                Toast.LENGTH_LONG).show();

        // TODO update rejected offer status
//        NotificationHelper.getInstance().showNotification(context, rejectId);
    }
}
