package com.cs407.kwikTix;

import java.io.Serializable;

public class Offer implements Serializable {
    //"(id INTEGER ,offerAmount TEXT,buyerUsername TEXT,PRIMARY KEY (id, buyerUsername),FOREIGN KEY (id) REFERENCES listings(id),FOREIGN KEY (buyerUsername) REFERENCES users(username))");
    private String id;
    private String offerAmount;
    private String buyerUsername;
    private String status;


    public Offer(String id, String offerAmount, String buyerUsername, String status) {
        this.id = id;
        this.offerAmount = offerAmount;
        this.buyerUsername = buyerUsername;
        this.status = status;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public String getStatus() {
        return status;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public String getId() {
        return id;
    }
}
