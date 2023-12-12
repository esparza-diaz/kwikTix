package com.cs407.kwikTix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
        String buyerUsername = intent.getStringExtra("buyerUsername");

        ArrayList<Offer> offers = dbHelper.getOffers(null, acceptedOfferId);
        if (offers.size() != 0) {
            for(Offer offer : offers) {
                if (offer.getBuyerUsername().equals(buyerUsername)) {
                    dbHelper.acceptOffer(offer);
                    // Cancel notification
                    notificationManagerCompat.cancel(notificationId);
                }
            }



        }

        Toast.makeText(context, context.getString(R.string.ACCEPTED),
                Toast.LENGTH_LONG).show();
    }
}
