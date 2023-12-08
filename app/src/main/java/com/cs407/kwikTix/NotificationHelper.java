package com.cs407.kwikTix;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class NotificationHelper {
    private static NotificationHelper INSTANCE = new NotificationHelper();

    private NotificationHelper() {}

    public static NotificationHelper getInstance() {
        return INSTANCE;
    }

    public static boolean areStringsSet = false;
    public static final String CHANNEL_ID = "Offers Channel";
    public static String SELLER_ACCEPT_REJECT = null;
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    public static String BUYER_OFFER_UPDATE = null;
    public static String BUYER_PURCHASED_TICKET = null;
    public static String SELLER_TICKET_PURCHASED = null;
    final ArrayList<NotificationItem> notificationItems = new ArrayList<>();
    public static String notificationType =null;
    public static String notificationContent = null;
    public static String ticketTitle = null;
    public static String buyerContactInfo = null;
    public static String sellerContactInfo = null;

    public static void setNotificationType(String notificationType) {
        NotificationHelper.notificationType = notificationType;
    }

    public static void setTicketTitle(String ticketTitle) {
        NotificationHelper.ticketTitle = ticketTitle;
    }

    public static void setResourceStrings(Context context){
        SELLER_ACCEPT_REJECT = context.getString(R.string.SELLER_ACCEPT_REJECT);
        BUYER_OFFER_UPDATE = context.getString(R.string.BUYER_OFFER_UPDATE);
        SELLER_TICKET_PURCHASED = context.getString(R.string.SELLER_TICKET_PURCHASED);
        BUYER_PURCHASED_TICKET = context.getString(R.string.BUYER_PURCHASED_TICKET);
        areStringsSet = true;
    }


    public void createNotificationChannel(Context context) { // TODO fix repeated channel creation after. Executes whenever main activity launches.
        if (!areStringsSet) {
            setResourceStrings(context);
        }

        CharSequence name = context.getString(R.string.CHANNEL_ID);
        String description = context.getString(R.string.CHANNEL_DESCRIPTION);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    public void setNotificationContent(Context context, Users buyer, Users seller, String ticketTitle,
                                       String offerAmount, String offerStatus, String notificationType,
                                       String offerId, String listingId) {

        if (!areStringsSet) {
            setResourceStrings(context);
        }

        if (offerId == null) {
            offerId = "";
        }

        if (listingId == null) {
            listingId = "";
        }

        setTicketTitle(ticketTitle);

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

        // Sets notification content based on notification type
        if (notificationType.equals(BUYER_OFFER_UPDATE)) {
            setNotificationType("Offer Update: " + ticketTitle);

            switch (offerStatus) {
                case REJECTED:
                    notificationContent = "REJECTED: Your offer has been rejected.";
                    break;
                case ACCEPTED:
                    notificationContent = "ACCEPTED: For purchasing, please contact "
                            + seller.getUsername() + " at -- " + sellerContactInfo;
                    break;
                default:
                    break;
            }
        }

        // Sets notification content based on notification type
        if (notificationType.equals(SELLER_TICKET_PURCHASED)) {
            setNotificationType("PURCHASED: " + ticketTitle);

            notificationContent = "Please contact " + buyer.getUsername() + " at -- " + buyerContactInfo;
        }

        // Sets notification content based on notification type
        if (notificationType.equals(SELLER_ACCEPT_REJECT)) {
            setNotificationType(notificationType + ": " + ticketTitle);

            notificationContent = buyer.getUsername() + " is offering $" + offerAmount + ".";
        }

        if (notificationType.equals(BUYER_PURCHASED_TICKET)) {
            setNotificationType("SUCCESSFUL PURCHASE: " + ticketTitle);

            notificationContent = "Please contact " + seller.getUsername() + " at -- " + sellerContactInfo;
        }

        // Adds notification to list
        NotificationItem item = new NotificationItem(
                buyer.getUsername(),
                seller.getUsername(),
                NotificationHelper.notificationType,
                notificationContent,
                notificationItems.size(),
                offerId,
                listingId) ;

        notificationItems.add(item); // TODO add notification in addNotification method?
    }

    public void showNotification(Context context, int id) {
        NotificationItem item;
        if (id == -1) {
            item = notificationItems.get(notificationItems.size() -1);
        } else {
            item = notificationItems.get(id);
        }

        String offerId = item.getOfferId();
        String listingId = item.getListingId();
        String buyerUsername = item.getBuyerUsername();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Intent notificationIntent = new Intent(context, StandardNotificationReceiver.class);
        notificationIntent.putExtra("notificationId", item.getId());

        PendingIntent notificationPendingIntent =
                PendingIntent.getBroadcast(context,
                        item.getId(),
                        notificationIntent,
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent acceptIntent = new Intent(context, AcceptReceiver.class);
        acceptIntent.putExtra("notificationId", item.getId());
        acceptIntent.putExtra("offerId", offerId);
        acceptIntent.putExtra("listingId", listingId);
        acceptIntent.putExtra("buyerUsername", buyerUsername);

        PendingIntent acceptPendingIntent =
                PendingIntent.getBroadcast(context,
                        item.getId(),
                        acceptIntent,
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent rejectIntent = new Intent(context, RejectReceiver.class);
        rejectIntent.putExtra("notificationId", item.getId());
        rejectIntent.putExtra("offerId", offerId);
        rejectIntent.putExtra("listingId", listingId);
        rejectIntent.putExtra("buyerUsername", buyerUsername);


        PendingIntent rejectPendingIntent =
                PendingIntent.getBroadcast(context,
                        item.getId(),
                        rejectIntent,
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acknowledgeNotificationAction =
                new NotificationCompat.Action.Builder(R.drawable.baseline_person_24,
                        "OK", notificationPendingIntent)
                        .build();

        NotificationCompat.Action acceptOfferAction =
                new NotificationCompat.Action.Builder(R.drawable.baseline_check_24,
                        "ACCEPT", acceptPendingIntent)
                        .build();


        NotificationCompat.Action rejectOfferAction =
                new NotificationCompat.Action.Builder(R.drawable.baseline_do_not_disturb_24,
                        "REJECT", rejectPendingIntent)
                        .build();

        NotificationCompat.Builder notificationsBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentTitle(item.getNotificationType())
                .setContentText(item.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (item.getNotificationType().equals(SELLER_ACCEPT_REJECT + ": " + ticketTitle)) {
                    notificationsBuilder.addAction(acceptOfferAction);
                    notificationsBuilder.addAction(rejectOfferAction);
        } else {
            notificationsBuilder.addAction(acknowledgeNotificationAction);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(item.getId(), notificationsBuilder.build());

    }
}
