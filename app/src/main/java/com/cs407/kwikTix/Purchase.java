package com.cs407.kwikTix;

import java.io.Serializable;

public class Purchase implements Serializable {
    //"(id INTEGER ,offerAmount TEXT,buyerUsername TEXT,PRIMARY KEY (id, buyerUsername),FOREIGN KEY (id) REFERENCES listings(id),FOREIGN KEY (buyerUsername) REFERENCES users(username))");

    private Offer offer;
    private Tickets ticket;
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
        this.offer = offer;
        this.ticket = ticket;
        //offer
        this.id = offer.getId();
        this.offerAmount = offer.getOfferAmount();
        this.buyerUsername = offer.getBuyerUsername();
        //ticket
        this.date = ticket.getDate();
        this.college = ticket.getCollege();
        this.sellerUsername = ticket.getSeller();
        this.price = ticket.getPrice();
    }

    public Offer getOffer(){ return offer; }
    public Tickets getTicket(){ return ticket; }

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
