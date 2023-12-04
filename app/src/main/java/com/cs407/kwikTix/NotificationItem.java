package com.cs407.kwikTix;

public class NotificationItem {
    private String buyerUsername = null;
    private String sellerUsername = null;
    private String notificationType = null;
    private String content = null;
    private int id = -1;

    public NotificationItem(String buyerUsername, String sellerUsername, String notificationType, String content, int id) {
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.notificationType = notificationType;
        this.content = content;
        this.id = id;
    }

    public String getbuyerUsername() {
        return buyerUsername;
    }

    public String getsellerUsername() {
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
}
