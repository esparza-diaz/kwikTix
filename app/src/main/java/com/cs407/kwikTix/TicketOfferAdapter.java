package com.cs407.kwikTix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        Button acceptOfferButton = convertView.findViewById(R.id.acceptOffer);
        Button rejectOfferButton = convertView.findViewById(R.id.rejectOffer);



        if (currentOffer != null) {
            offeredPrice.setText("$" + currentOffer.getOfferAmount());
            offerBuyer.setText(userLoggedIn);
        }

        // ACCEPT OFFER LISTENER
        acceptOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Offer Accepted", Toast.LENGTH_SHORT).show();
                dbHelper.acceptOffer(currentOffer);
                clearOffer(position);
            }
        });

        // Set click listener for "Reject Offer" button
        rejectOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Offer Rejected", Toast.LENGTH_SHORT).show();
                dbHelper.declineOffer(currentOffer);
                clearOffer(position);
            }
        });

        return convertView;
    }

    public void clearOffer(int position) {
        if (position >= 0 && position < getCount()) {
            remove(getItem(position));
        }
    }

}
