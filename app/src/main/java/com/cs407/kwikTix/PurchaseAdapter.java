package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

//public class PurchaseAdapter extends ArrayAdapter<Offer> {
public class PurchaseAdapter extends ArrayAdapter<Tickets> {

        //public PurchaseAdapter(Context context, List<Offer> purchase) {super(context, 0, purchase);}
        public PurchaseAdapter(Context context, List<Tickets> purchase) {super(context, 0, purchase);}


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_purchase, parent, false);
        }
        //Purchase currentPurchase = getItem(position);
        //Offer currentPurchase = getItem(position);
        Tickets currentPurchase = getItem(position);

//        SQLiteDatabase sqLiteDatabase = convertView.getContext().openOrCreateDatabase("KwikTix", Context.MODE_PRIVATE, null);
//        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
//        Tickets ticket = dbHelper.getTicket(currentPurchase.getId());

        TextView ticketName = convertView.findViewById(R.id.ticketName);
        TextView sellerName = convertView.findViewById(R.id.sellerName);
        TextView purchasedPrice = convertView.findViewById(R.id.purchasedPrice);

        if (currentPurchase != null) {
            ticketName.setText(currentPurchase.getTitle());
            sellerName.setText(currentPurchase.getSeller());
            purchasedPrice.setText(currentPurchase.getSellPrice());
            //TODO: change to whatever method is added to Tickets
        }
        return convertView;
    }
}
