package com.cs407.kwikTix;

import android.content.Context;
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

        TextView ticketName = convertView.findViewById(R.id.ticketName);
        //TextView labelGame = convertView.findViewById(R.id.labelGame);
        TextView gameName = convertView.findViewById(R.id.gameName);
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


        if (currentOffer != null) {
            ticketName.setText(currentOffer.getTitle());
            //labelGame.setText("Game: ");
            gameName.setText("Game"); //TODO: change to be a variable currentOffer.getTicket().getGame()
            //labelLocation.setText("Location: ");
            //gameLocation.setText("Location"); //TODO: change to be a variable currentOffer.getTicket().getLocation()
            //labelListingPrice.setText("Listing Price: ");
            //listingPrice.setText(currentOffer.getTicket().getPrice());
            //labelSeller.setText("Seller: ");
            //sellerName.setText(currentOffer.getSeller());
            //labelDateTime.setText("Date/Time: ");
            //dateTime.setText(currentOffer.getTicket().getDate());
            //labelOfferedPrice.setText("Offered Price: ");
            //offeredPrice.setText(currentOffer.getPrice());
            //labelStatus.setText("Status: ");
            offerStatus.setText("Waiting"); //TODO: change to be a variable
        }

        return convertView;
    }
}
