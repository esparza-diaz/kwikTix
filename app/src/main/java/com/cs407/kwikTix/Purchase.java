package com.cs407.kwikTix;

import java.io.Serializable;

public class Purchase implements Serializable {
    //"(id INTEGER ,offerAmount TEXT,buyerUsername TEXT,PRIMARY KEY (id, buyerUsername),FOREIGN KEY (id) REFERENCES listings(id),FOREIGN KEY (buyerUsername) REFERENCES users(username))");

    //params for Offers
    private String id;
    private String offerAmount;
    private String buyerUsername;
    private String status; //don't need

    //params for Tickets
    private String title; //don't need
    private String date;
    private String college;
    private String sellerUsername;
    private String buyer;
    private String price;
    private String available; //don't need


    public Purchase(Tickets ticket, Offer offer) {
        //offer
        this.id = offer.getId();
        this.offerAmount = offer.getOfferAmount();
        this.buyerUsername = offer.getBuyerUsername();
        this.status = offer.getStatus();
        //ticket
        this.title = ticket.getTitle(); //don't need
        this.date = ticket.getDate();
        this.college = ticket.getCollege();
        this.sellerUsername = ticket.getSeller();
        this.buyer = ticket.getBuyer();
        this.price = ticket.getPrice();
        this.available = ticket.getAvailable(); //don't need
    }

    //offer
    public String getId() {
        return id;
    }
    public String getOfferAmount() {
        return offerAmount;
    }
    public String getBuyerUsername() {
        return buyerUsername;
    }
    public String getStatus() {
        return status;
    }


    //ticket
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getCollege() {
        return college;
    }
    public String getSellerUsername() {
        return sellerUsername;
    }
    public String getBuyer() {
        return buyer;
    }
    public String getPrice() {
        return price;
    }
    public String getAvailable() {
        return available;
    }

}
