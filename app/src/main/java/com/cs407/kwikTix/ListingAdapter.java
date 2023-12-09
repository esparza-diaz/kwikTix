package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListingAdapter extends ArrayAdapter<Tickets> {

    public ListingAdapter(Context context, List<Tickets> listing) {
        super(context, 0, listing);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_listing, parent, false);
        }
        Tickets currentListing = getItem(position);

        TextView ticketName = convertView.findViewById(R.id.ticketName);
        TextView offerStatus = convertView.findViewById(R.id.offerStatus);

        String ticketAvailability="";
        if(currentListing.getAvailable().equals("1")){
            ticketAvailability="Available";
        }
        else if(currentListing.getAvailable().equals("0")){
            ticketAvailability="Purchased";
            //TODO figure out what labels we want for different statuses
        }

        if (currentListing != null) {
            ticketName.setText(currentListing.getTitle());
            offerStatus.setText(ticketAvailability);
        }

        return convertView;
    }
}
