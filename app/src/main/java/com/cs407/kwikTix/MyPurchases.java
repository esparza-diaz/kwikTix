package com.cs407.kwikTix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPurchases#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPurchases extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;

    public MyPurchases() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Listings.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPurchases newInstance(String param1) {
        MyPurchases fragment = new MyPurchases();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    PurchaseAdapter adapter;
    //ArrayList<Offer> displayPurchases = new ArrayList<Offer>();
    ArrayList<Tickets> displayPurchases = new ArrayList<Tickets>();
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    ListView myPurchasesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_purchase, container, false);
        myPurchasesListView = (ListView) v.findViewById(R.id.myPurchases);

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedIn = sharedPreferences.getString("username","");

        displayPurchases.clear();
        displayPurchases = dbHelper.getPurchases(userLoggedIn);

        adapter = new PurchaseAdapter(v.getContext(), displayPurchases);
        myPurchasesListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        myPurchasesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Offer selectedPurchase = displayPurchases.get(i);
                Tickets selectedPurchase = displayPurchases.get(i);


                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SinglePurchase singlePurchaseFragment = new SinglePurchase();
                Bundle args = new Bundle();
                args.putSerializable("selectedPurchase", selectedPurchase);
                singlePurchaseFragment.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singlePurchaseFragment)
                        //.setReorderingAllowed(true)
                        .addToBackStack("showing Purchase")
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


    public void refreshPurchases() {
        // Notify the adapter that the data has changed
        Log.i("TEST", displayPurchases.toString());

        if (displayPurchases.size() == 0){
            Toast.makeText(requireContext(),"No purchases found", Toast.LENGTH_LONG).show();
        }
        adapter.clear();
        adapter.addAll(displayPurchases);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}