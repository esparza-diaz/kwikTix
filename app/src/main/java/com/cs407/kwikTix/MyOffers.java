package com.cs407.kwikTix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyOffers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOffers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;

    public static ArrayList<Offer> offers;

    public MyOffers() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Listings.
     */
    // TODO: Rename and change types and number of parameters
    public static MyOffers newInstance(String param1) {
        MyOffers fragment = new MyOffers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    OfferAdapter adapter;
    ArrayList<Offer> displayOffers = new ArrayList<Offer>();
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    ListView myOffersListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_my_offers, container, false);
        myOffersListView = (ListView) v.findViewById(R.id.myOffers);

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedIn = sharedPreferences.getString("username","");
        //filter offers by username, not by ticket id
        displayOffers.clear();
        displayOffers = dbHelper.getOffers(userLoggedIn, null);

        adapter = new OfferAdapter(v.getContext(), displayOffers);
        myOffersListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        myOffersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Offer selectedOffer = displayOffers.get(i);

                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SingleOffer singleOfferFragment = new SingleOffer();
                Bundle args = new Bundle();
                args.putSerializable("selectedOffer", selectedOffer);
                //args.putString("buyerUsername",userLoggedIn);
                singleOfferFragment.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleOfferFragment)
                        //.setReorderingAllowed(true)
                        .addToBackStack("showing Single Offer")
                        .commit();
            }
        });

        // Handle Back button click
        ImageButton backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStack();
            }
        });

        return v;
    }


    public void refreshOffers() {
        // Notify the adapter that the data has changed
        Log.i("TEST", displayOffers.toString());

        if (displayOffers.size() == 0){
            Toast.makeText(requireContext(),"No offers found", Toast.LENGTH_LONG).show();
        }
        adapter.clear();
        adapter.addAll(displayOffers);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}