package com.cs407.kwikTix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cs407.kwikTix.Listing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView dateTimeTextView = convertView.findViewById(R.id.dateTime);

        if (currentListing != null) {
            titleTextView.setText(currentListing.getTitle());
            priceTextView.setText(String.format("$%s", currentListing.getPrice()));

            String dateString = currentListing.getDate();

            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MM/dd/yyyy 'at' hh:mm a", Locale.getDefault());
            try {
                // Parse the input string into a Date object
                Date date = inputFormat.parse(dateString);

                // Format the Date object into the desired output format
                String formattedDate = outputFormat.format(date);

                // Set the formatted date to your TextView
                dateTimeTextView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                dateTimeTextView.setText(dateString);
                // Handle parsing exception
            }
        }

        return convertView;
    }
}
