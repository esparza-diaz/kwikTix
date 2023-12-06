package com.cs407.kwikTix;

public class NotificationItem {
    private String buyerUsername = null;
    private String sellerUsername = null;
    private String notificationType = null;
    private String content = null;
    private String offerId = null;
    private String listingId = null;
    private int id = -1;

    public NotificationItem(String buyerUsername, String sellerUsername, String notificationType, String content, int id, String offerId, String listingId) {
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.notificationType = notificationType;
        this.content = content;
        this.id = id;
        this.offerId = offerId;
        this.listingId = listingId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getOfferId() { return offerId; }

    public String getListingId() {
        return listingId;
    }
}
