package com.cs407.kwikTix;

import android.content.Context;
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
        //TextView labelGame = convertView.findViewById(R.id.labelGame);
        //TextView gameName = convertView.findViewById(R.id.gameName);
        //TextView labelLocation = convertView.findViewById(R.id.labelLocation);
        //TextView gameLocation = convertView.findViewById(R.id.gameLocation);
        //TextView labelListingPrice = convertView.findViewById(R.id.labelListingPrice);
        //TextView listingPrice = convertView.findViewById(R.id.listingPrice);
        //TextView labelSeller = convertView.findViewById(R.id.labelSeller);
        //TextView sellerName = convertView.findViewById(R.id.sellerName);
        //TextView labelDateTime = convertView.findViewById(R.id.labelDateTime);
        //TextView dateTime = convertView.findViewById(R.id.dateTime);
        //TextView labelOfferedPrice = convertView.findViewById(R.id.labelOfferedPrice);
        //TextView offeredPrice = convertView.findViewById(R.id.offeredPrice);
        //TextView labelStatus = convertView.findViewById(R.id.labelStatus);
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
            //labelGame.setText("Game: ");
            //gameName.setText(currentListing.getTitle());
            //labelLocation.setText("Location: ");
            //gameLocation.setText(currentListing.getCollege());
            //labelListingPrice.setText("Listing Price: ");
            //listingPrice.setText(currentListing.getPrice().toString());
            //labelSeller.setText("Seller: ");
            //sellerName.setText(currentOffer.getSeller());
            //labelDateTime.setText("Date/Time: ");
            //dateTime.setText(currentListing.getDate());
            //labelOfferedPrice.setText("Offered Price: ");
            //offeredPrice.setText(currentOffer.getPrice());
            //labelStatus.setText("Status: ");
            offerStatus.setText(ticketAvailability);
        }

        return convertView;
    }
}
