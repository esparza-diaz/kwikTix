package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import pl.droidsonroids.gif.GifDrawable;
//import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMyListing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleMyListing extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public SingleMyListing() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleTicket.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleMyListing newInstance(String param1, String param2) {
        SingleMyListing fragment = new SingleMyListing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userLoggedIn = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_listing, container, false);
        SQLiteDatabase sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        // Retrieve the selectedListing from arguments
        Bundle args = getArguments();
        if (args != null) {
            Tickets selectedListing = (Tickets) args.getSerializable("selectedListing");
            if (selectedListing != null) {
                Log.i("TEST",selectedListing.getTitle());
                Log.i("TEST",selectedListing.getSeller());
                // Update your UI with the selectedListing details
                TextView ticketNameTextView = v.findViewById(R.id.ticketName);
                ticketNameTextView.setText(selectedListing.getTitle());

                TextView locationNameTextView = v.findViewById(R.id.gameLocation);
                locationNameTextView.setText(selectedListing.getCollege()); // Set the college name

                TextView gameNameTextView = v.findViewById(R.id.gameName);
                gameNameTextView.setText(selectedListing.getTitle());

                TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
                ticketPriceTextView.setText("$" + selectedListing.getPrice().toString());

                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MM/dd/yyyy 'at' hh:mm a", Locale.getDefault());
                TextView dateTimeTextView = v.findViewById(R.id.dateTime);
                String dateString = selectedListing.getDate().toString();
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
                // Handle Back button click
                ImageButton backButton = v.findViewById(R.id.backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });

                LinearLayout ticketNotBoughtLayout = v.findViewById(R.id.ticketNotBought);
                LinearLayout ticketBoughtLayout = v.findViewById(R.id.ticketBought);

                // if ticket bought
                if (selectedListing.getAvailable().equals("0")){
                    ticketNotBoughtLayout.setVisibility(View.GONE);
                    ticketBoughtLayout.setVisibility(View.VISIBLE);
                    TextView finalPriceTextView = v.findViewById(R.id.finalPrice);
                    TextView buyerTextView = v.findViewById(R.id.buyerUsername);
                    TextView contactTextView = v.findViewById(R.id.buyerInfo);

                    finalPriceTextView.setText("$" + selectedListing.getSellPrice());
                    buyerTextView.setText(selectedListing.getBuyer());
                    Users user = dbHelper.getUser(selectedListing.getBuyer());
                    if (user.getPrefContactMethod().equals("Phone")){
                        contactTextView.setText(user.getPhone());
                    }else{
                        contactTextView.setText(user.getEmail());
                    }

                }else{
                    ticketNotBoughtLayout.setVisibility(View.VISIBLE);
                    ticketBoughtLayout.setVisibility(View.GONE);
                }

                Button seeOffers = v.findViewById(R.id.seeOffersButton);
                seeOffers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        FragmentManager fragmentManager = getParentFragmentManager();
                        TicketOffers offersFragment = new TicketOffers();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedListing", selectedListing);
                        offersFragment.setArguments(bundle);

                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, offersFragment)
                                .addToBackStack("showing Post")
                                .commit();
                    }
                });

                Button revokeListing = v.findViewById(R.id.revokeListingButton);
                revokeListing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        try {
                            dbHelper.deleteListing(selectedListing.getId());
                            Toast.makeText(getContext(),"Successfully Revoked Listing",Toast.LENGTH_SHORT).show();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.popBackStack();
                        }catch (Exception e){
                            Toast.makeText(getContext(),"Error Revoking Listing",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        return v;
    }
}