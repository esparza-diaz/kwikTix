package com.cs407.kwikTix;

import java.io.Serializable;

public class Offer implements Serializable {
    private String buyerUsername;
    private String sellerUsername;
    private String title;
    private String price;
    private Tickets ticket;

    public Offer(Tickets t, String title, String buyerUsername, String offerAmount, String seller) {
        this.ticket = t;
        this.title = title;
        this.price = offerAmount;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = seller;
    }

    public Tickets getTicket() { return this.ticket; }

    public String getTitle() {
        return this.title;
    }

    public String getPrice() {
        return this.price;
    }

    public String getBuyer() {
        return this.buyerUsername;
    }

    public String getSeller() {
        return this.sellerUsername;
    }
}
