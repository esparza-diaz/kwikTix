package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OfferAdapter extends ArrayAdapter<Offer> {

    public OfferAdapter(Context context, List<Offer> offer) {
        super(context, 0, offer);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_offer, parent, false);
        }

        Offer currentOffer = getItem(position);

        SQLiteDatabase sqLiteDatabase = convertView.getContext().openOrCreateDatabase("KwikTix", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        Tickets ticket = dbHelper.getTicket(currentOffer.getId());

        TextView ticketName = convertView.findViewById(R.id.ticketName);
        TextView gameName = convertView.findViewById(R.id.gameName);
        TextView offerStatus = convertView.findViewById(R.id.offerStatus);


        if (currentOffer != null) {
            ticketName.setText(ticket.getTitle());
            gameName.setText(ticket.getTitle());
            offerStatus.setText(currentOffer.getStatus());
        }

        return convertView;
    }
}
