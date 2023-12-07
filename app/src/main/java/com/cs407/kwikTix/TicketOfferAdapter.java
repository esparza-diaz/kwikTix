package com.cs407.kwikTix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TicketOfferAdapter extends ArrayAdapter<Offer> {

    public TicketOfferAdapter(Context context, List<Offer> offer) {
        super(context, 0, offer);
    }

    public void setUsername(String username){
        userLoggedIn=username;
    }

    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    private String userLoggedIn;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_ticket_offer, parent, false);
        }

        Offer currentOffer = getItem(position);

        sqLiteDatabase = convertView.getContext().openOrCreateDatabase("KwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        Tickets ticket = dbHelper.getTicket(currentOffer.getId());

        TextView offeredPrice = convertView.findViewById(R.id.offeredPrice);
        TextView offerBuyer = convertView.findViewById(R.id.offerBuyer);


        if (currentOffer != null) {
            offeredPrice.setText(currentOffer.getOfferAmount());
            offerBuyer.setText(userLoggedIn);
        }

        return convertView;
    }
}
