package com.cs407.kwikTix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleOffer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleOffer extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public SingleOffer() {
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
    public static SingleOffer newInstance(String param1, String param2) {
        SingleOffer fragment = new SingleOffer();
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
            userLoggedIn = getArguments().getString("buyerUsername");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_offer, container, false);

        // Retrieve the selectedListing from arguments
        Bundle args = getArguments();
        if (args != null) {
            Offer selectedOffer = (Offer) args.getSerializable("selectedOffer");
            if (selectedOffer != null) {
                // Update your UI with the selectedListing details
                /*TextView ticketNameTextView = v.findViewById(R.id.ticketName);
                ticketNameTextView.setText(selectedOffer.getTitle());

                TextView locationNameTextView = v.findViewById(R.id.gameLocation);
                locationNameTextView.setText(selectedOffer.getTicket().getCollege());

                TextView gameNameTextView = v.findViewById(R.id.gameName);
                gameNameTextView.setText(selectedOffer.getTitle());

                TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
                ticketPriceTextView.setText(selectedOffer.getTicket().getPrice());

                TextView sellerNameTextView = v.findViewById(R.id.sellerName);
                sellerNameTextView.setText(selectedOffer.getSeller());

                TextView dateTimeTextView = v.findViewById(R.id.dateTime);
                dateTimeTextView.setText(selectedOffer.getTicket().getDate());

                TextView offeredPriceTextView = v.findViewById(R.id.offeredPrice);
                offeredPriceTextView.setText(selectedOffer.getPrice());

                TextView offerStatusTextView = v.findViewById(R.id.offerStatus);
                offerStatusTextView.setText("Waiting"); //TODO: change to a variable selectedOffer.getStatus()

                EditText counterOfferAmount = v.findViewById(R.id.counterOfferAmount);*/

                // Handle Back button click
                ImageButton backButton = v.findViewById(R.id.backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });

                Button changeOffer = v.findViewById(R.id.changeOfferButton);
                changeOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(requireContext(), "Changing Offer as: " + userLoggedIn, Toast.LENGTH_SHORT).show();
                        //TODO: initiate change in offer amount
                    }
                });

                Button revokeOffer = v.findViewById(R.id.revokeOfferButton);
                revokeOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(requireContext(), "Revoking Offer as: " + userLoggedIn, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return v;
    }
}