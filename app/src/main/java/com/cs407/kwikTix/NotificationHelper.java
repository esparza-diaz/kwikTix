package com.cs407.kwikTix;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class NotificationHelper {
    private static final NotificationHelper INSTANCE = new NotificationHelper();

    private NotificationHelper() {}

    public static NotificationHelper getInstance() {
        return INSTANCE;
    }

    public static final String CHANNEL_ID = "OFFERS_CHANNEL";
    public static final String SELLER_ACCEPT_REJECT = "SELLER_DECISION";
    public static final String BUYER_OFFER_STATUS = "BUYER_OFFER_STATUS";
    public static final String SELLER_TICKET_PURCHASED = "SELLER_TICKET_PURCHASED";

    public void createNotificationChannel(Context context) {
        CharSequence name = "Offers";
        String description = "Displays offers for user.";

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    final ArrayList<NotificationItem> notificationItems = new ArrayList<>();
    private Users buyer;
    private Users seller;
    private String notificationType = null;
    private String notificationContent = null;
    private String ticketTitle = null;
    private String buyerContactInfo = null;
    private String sellerContactInfo = null;
    private int offerAmount = -1;
    private int notificationId = 0;
    private int offerStatus = -1; // TODO figure out how to set offer status based on offers db


    public void setNotificationContent(String buyerUsername, String sellerUsername, String ticketTitle,
                                       int offerAmount, int offerStatus, String notificationType) {
        // Sets seller contact info
        if (seller.getPrefContactMethod().equals("E-Mail")) {
            sellerContactInfo = seller.getEmail();
        } else if (seller.getPrefContactMethod().equals("Phone")) {
            sellerContactInfo = seller.getPhone();
        }

        // Sets buyer contact info
        if (buyer.getPrefContactMethod().equals("E-Mail")) {
            buyerContactInfo = buyer.getEmail();
        } else if (buyer.getPrefContactMethod().equals("Phone")) {
            buyerContactInfo = buyer.getPhone();
        }


        if (notificationType.equals(BUYER_OFFER_STATUS)) {
            switch (offerStatus) {
                case 1:
                    notificationContent = "REJECTED: Your offer for the "
                            + ticketTitle + " ticket has been rejected.";
                    break;
                case 2:
                    notificationContent = "ACCEPTED: Your offer for the "
                            + ticketTitle + " ticket has been accepted. \n"
                            + "Please contact " + seller.getUsername() + " at -- " + sellerContactInfo;
                    break;
                default:
                    break;
            }
        } else if (notificationType.equals(SELLER_TICKET_PURCHASED)) {
            notificationContent = "PURCHASED: Your " + ticketTitle + " ticket has been purchased"
                    + " by " + buyer + "\n Please contact " + buyer + " at -- " + buyerContactInfo;
        } else if (notificationType.equals(SELLER_ACCEPT_REJECT)) {
            notificationContent = "OFFER: " + buyerUsername + " wants to buy your " + ticketTitle
                    + " ticket for -- $" + offerAmount + ".";
        }

    }

    public void showNotification(Context context, int id) {
        NotificationCompat.Builder sellerNotificationsBuilder = null;
        NotificationCompat.Builder buyerNotificationsBuilder = null;

        NotificationItem item;
        if (id == -1) {
            item = notificationItems.get(notificationItems.size() -1);
        } else {
            item = notificationItems.get(id);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        NotificationCompat.Action acceptOffer = // TODO add accept and reject options
//                new NotificationCompat.Action.Builder()

        if (notificationType.equals(SELLER_ACCEPT_REJECT)) {
            // Creates notifications allowing seller to accept/reject offers
            sellerNotificationsBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setContentTitle(notificationType)
                    .setContentText(notificationContent)
//                    .addAction(acceptAction)
//                    .addAction(rejectAction)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else if (notificationType.equals(BUYER_OFFER_STATUS) || notificationType.equals(SELLER_TICKET_PURCHASED)) {
            // Creates notifications notifying buyer if their offers were accepted/rejected
            // Created notifications notifying seller that their ticket was purchased
            buyerNotificationsBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setContentTitle(notificationType)
                    .setContentText(notificationContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (sellerNotificationsBuilder != null) {
            notificationManager.notify(notificationId, sellerNotificationsBuilder.build());
        }

        if (buyerNotificationsBuilder != null) {
            notificationManager.notify(notificationId, buyerNotificationsBuilder.build());
        }

    }

    public void addNotifications() {

    }
}
