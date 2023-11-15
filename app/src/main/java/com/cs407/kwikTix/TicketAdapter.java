package com.cs407.kwikTix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cs407.kwikTix.Listing;

import java.util.List;

public class TicketAdapter extends ArrayAdapter<Tickets> {

    public TicketAdapter(Context context, List<Tickets> tickets) {
        super(context, 0, tickets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_ticket, parent, false);
        }

        Tickets currentListing = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);

        if (currentListing != null) {
            titleTextView.setText(currentListing.getTitle());
            priceTextView.setText(String.format("$%s", currentListing.getPrice()));
        }

        return convertView;
    }
}
