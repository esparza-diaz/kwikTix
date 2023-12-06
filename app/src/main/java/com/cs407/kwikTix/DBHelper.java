package com.cs407.kwikTix;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }
    //TODO: Modify listing database to include foreign key to username after creating users db
    public static void createTable(){
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS colleges "+
                "(college TEXT PRIMARY KEY,latitude TEXT, longitude TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users "+
                "(username TEXT PRIMARY KEY,password TEXT,email TEXT, phone TEXT, prefContactMethod TEXT, college TEXT, FOREIGN KEY(college) REFERENCES colleges(college))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS listings "+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT,date DATETIME,price TEXT, college TEXT,seller TEXT,buyer TEXT,available TEXT, FOREIGN KEY(college) REFERENCES colleges(college), FOREIGN KEY(seller) REFERENCES users(username))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS offers "+
                "(id INTEGER ,offerAmount TEXT,buyerUsername TEXT, status TEXT,PRIMARY KEY (id, buyerUsername),FOREIGN KEY (id) REFERENCES listings(id),FOREIGN KEY (buyerUsername) REFERENCES users(username))");

    }

    /**
     * Adds user to users db. Should be called after user registers
     * @param username
     * @param password
     * @param email
     * @param phone
     * @param prefContactMethod
     * @param college
     *
     * @return True is addition of user is successful; false is username is already taken
     */
    public boolean addUser(String username,String password,String email, String phone, String prefContactMethod, String college) throws SQLiteConstraintException {
        createTable();
        try {
            sqLiteDatabase.execSQL("INSERT INTO users (username, password, email, phone, prefContactMethod, college) VALUES (?,?,?,?,?,?)",
                    new String[]{username, password, email, phone, prefContactMethod, college});
            return true;
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message) TODO: Logic to catch for same user
            Log.i("Info User(Primary Key)", "Same primary key for " + username);
            return false;
        }
    }

    /**
     * Deletes user from the users SQL database
     *
     * @param username
     */
    public void deleteUser(String username){
        createTable(); // TODO add delete option in manage settings area
        sqLiteDatabase.delete("users", "DELETE FROM users WHERE username=" + username,
                new String[]{username});
    }

    /**
     * Logic when ticket is bought
     * @param listing
     */
    public void boughtTicket(Tickets listing, String buyer){
        createTable();
        //update listing
        sqLiteDatabase.execSQL("UPDATE listings SET available = ? WHERE id = ?",
                new String[]{"0",listing.getId()});
        sqLiteDatabase.execSQL("UPDATE listings SET buyer = ? WHERE id = ?",
                new String[]{buyer,listing.getId()});
        //update offers
        sqLiteDatabase.execSQL("UPDATE offers SET status = ? WHERE id = ?",
                new String[]{"REJECTED",listing.getId()});
    }

    /**
     * Declines offer given the key
     */
    public void acceptOffer(Offer offer){
        createTable();
        Tickets ticket = getTicket(offer.getId());
        String buyer = offer.getBuyerUsername();
        String id = offer.getId();
        String amt = offer.getOfferAmount();
        boughtTicket(ticket,buyer);

        // update price to reflect offer price
        sqLiteDatabase.execSQL("UPDATE listings SET price = ? WHERE id = ?",
                new String[]{amt,id});

        // set the accepted offer
        sqLiteDatabase.execSQL("UPDATE offers SET status = ? WHERE id = ? AND buyerUsername = ?",
                new String[]{"ACCEPTED",id,buyer});
    }

    /**
     * Accept offer
     */
    public void declineOffer(Offer offer){
        // set offer status to REJECTED
        sqLiteDatabase.execSQL("UPDATE offers SET status = ? WHERE id = ? AND buyerUsername = ?",
                new String[]{"REJECTED",offer.getId(), offer.getBuyerUsername()});
    }

    /**
     * Adds ticket to listings db
     * @param title
     * @param date
     * @param college
     * @param seller
     */
    public void addTicket(String title,String date,String price,String college, String seller, String available){
        createTable();
        //title TEXT,date DATETIME,price TEXT, college TEXT,username TEXT, available INTEGER
        try {
            sqLiteDatabase.execSQL("INSERT INTO listings (title, date, price, college, seller, available) VALUES (?,?,?,?,?,?)",
                    new String[]{title,date,price,college,seller,available});
            Log.i("Yay", "Ticket created");
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message) TODO: Same title
            Log.i("Info Listings(Primary Key)", "Same primary key for " + title);
        }
    }

    /**
     *
     * @param id
     * @param offerAmount
     * @param buyerUsername
     * @param status
     */
    public void addOffer(String id, String offerAmount, String buyerUsername, String status){
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO offers (id, offerAmount, buyerUsername, status) VALUES (?,?,?,?)",
                new String[]{id,offerAmount,buyerUsername,status});
    }

    public void updateOffer(String id, String offerAmount, String buyerUsername){
        createTable();
        // update amount and set status back to pending
        sqLiteDatabase.execSQL("UPDATE offers SET offerAmount = ?, status = 'PENDING' WHERE id = ? AND buyerUsername = ?",
                new String[]{offerAmount,id,buyerUsername});
    }

    /**
     * Adds college to colleges db
     * @param college - name of college
     * @param latitude - lat of college
     * @param longitude - long of college
     */
    public void addCollege(String college,String latitude,String longitude){
        createTable();
        try {
            sqLiteDatabase.execSQL("INSERT INTO colleges (college, latitude, longitude) VALUES (?,?,?)",
                    new String[]{college,latitude,longitude});
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message)
            Log.i("Info College(Primary Key)", "Same primary key for " + college);
        }
    }

    /**
     * Returns user information given username
     * @param username
     * @return Users item containing user information
     */
    public Users getUser(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE username LIKE ?",
                new String[]{"%" + username + "%"});
        Users user = null;
        if (c.moveToFirst()) {
            int passwordIndex = c.getColumnIndex("password");
            int emailIndex = c.getColumnIndex("email");
            int phoneIndex = c.getColumnIndex("phone");
            int prefContactMethodIndex = c.getColumnIndex("prefContactMethod");
            int collegeIndex = c.getColumnIndex("college");

            String password = c.getString(passwordIndex);
            String email = c.getString(emailIndex);
            String phone = c.getString(phoneIndex);
            String prefContactMethod = c.getString(prefContactMethodIndex);
            String college = c.getString(collegeIndex);

            user = new Users(username, password, email, phone, prefContactMethod, college);
        }

        c.close();
        return user;
    }

    public void setUser(String username, String email, String phone, String prefContactMethod) {
        Users user = getUser(username);
        Log.d("ManageProfile", "User Info Pre: "
                + user.getEmail() + ", "
                + user.getPhone() + ", "
                + user.getPrefContactMethod());
        sqLiteDatabase.execSQL("UPDATE users set email=?, phone=?, prefContactMethod=? WHERE username=username",
                new String[]{email, phone, prefContactMethod});
        user = getUser(username);
        Log.d("ManageProfile", "User Info Post: "
                + user.getEmail() + ", "
                + user.getPhone() + ", "
                + user.getPrefContactMethod());
    }
        /*
    public void updateNotes(String content,String date,String title,String username){
>>>>>>> main
        createTable();
        Notes note = new Notes(date,username,title,content);
        sqLiteDatabase.execSQL("UPDATE notes set content=?, date=? where title=? and username=?",
                new String[]{content,date,title,username});
    }

*/
//"(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT,date DATETIME,price TEXT, college TEXT,username TEXT, available TEXT)
    public Tickets getTicket(String key){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE id LIKE ?",
                new String[]{"%" + key + "%"});

        int idIndex = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        int dateIndex = c.getColumnIndex("date");
        int priceIndex = c.getColumnIndex("price");
        int collegeIndex = c.getColumnIndex("college");
        int sellerIndex = c.getColumnIndex("seller");
        int buyerIndex = c.getColumnIndex("buyer");
        int availableIndex = c.getColumnIndex("available");

        c.moveToFirst();
        String id = c.getString(idIndex);
        String title = c.getString(titleIndex);
        String date = c.getString(dateIndex);
        String price = c.getString(priceIndex);
        String college = c.getString(collegeIndex);
        String seller = c.getString(sellerIndex);
        String buyer = c.getString(buyerIndex);
        String available = c.getString(availableIndex);

        Tickets ticket = new Tickets(title,id,date,price,college,seller,buyer,available);
        c.close();

        return ticket;
    }

    /**
     * Gets listings for a specific user
     * @param seller
     * @return
     */
    public ArrayList<Tickets> getListings(String seller, String college, String sort_by, boolean desc){
        createTable();
        ArrayList<Tickets> listings = new ArrayList<>();
        if (seller == null && college == null && sort_by == null){
            //returns all listings
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings", null);
            int titleIndex = c.getColumnIndex("title");
            int listingId = c.getColumnIndex("id");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int sellerIndex = c.getColumnIndex("seller");
            int buyerIndex = c.getColumnIndex("buyer");
            int priceIndex = c.getColumnIndex("price");
            int availableIndex = c.getColumnIndex("available");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String id = c.getString(listingId);
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String user = c.getString(sellerIndex);
                String price = c.getString(priceIndex);
                String available = c.getString(availableIndex);
                String buyer = c.getString(buyerIndex);

                Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                listings.add(ticket);
                c.moveToNext();
            }
            c.close();
            return listings;
        }else if (seller != null){
            //returns listings for username
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE seller LIKE ?",
                    new String[]{"%" + seller + "%"});
            int titleIndex = c.getColumnIndex("title");
            int listingId = c.getColumnIndex("id");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int sellerIndex = c.getColumnIndex("seller");
            int buyerIndex = c.getColumnIndex("buyer");
            int priceIndex = c.getColumnIndex("price");
            int availableIndex = c.getColumnIndex("available");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String id = c.getString(listingId);
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String user = c.getString(sellerIndex);
                String price = c.getString(priceIndex);
                String available = c.getString(availableIndex);
                String buyer = c.getString(buyerIndex);

                Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                listings.add(ticket);
                c.moveToNext();
            }
            c.close();
            return listings;
        }else if (college != null && sort_by == null) {
            // filter by only college
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ?",
                    new String[]{"%" + college + "%"});
            int titleIndex = c.getColumnIndex("title");
            int listingId = c.getColumnIndex("id");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int sellerIndex = c.getColumnIndex("seller");
            int buyerIndex = c.getColumnIndex("buyer");
            int priceIndex = c.getColumnIndex("price");
            int availableIndex = c.getColumnIndex("available");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String id = c.getString(listingId);
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String user = c.getString(sellerIndex);
                String price = c.getString(priceIndex);
                String available = c.getString(availableIndex);
                String buyer = c.getString(buyerIndex);

                Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                listings.add(ticket);
                c.moveToNext();
            }
            c.close();
            return listings;
        }else if (college == null && sort_by.equals("price")) {
            // sort_by price only
            if (desc) {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings ORDER BY cast(price as numeric) DESC", null);
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings ORDER BY cast(price as numeric) ASC", null);
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }
            return listings;
        } else if (college != null && sort_by.equals("price")) {
            // sort_by price and filter by college
            if (desc) {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY cast(price as numeric) DESC",
                        new String[]{"%" + college + "%"});
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            } else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY cast(price as numeric) ASC",
                        new String[]{"%" + college + "%"});
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }
            return listings;
        }else if (college == null && sort_by.equals("date")) {
            // sort by date only
            if (desc) {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings ORDER BY date DESC", null);
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings ORDER BY date ASC", null);
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }
            return listings;
        }else if (college != null && sort_by.equals("date")) {
            // sort_by date and filter by college
            if (desc) {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY date DESC",
                        new String[]{"%" + college + "%"});
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            } else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY date ASC",
                        new String[]{"%" + college + "%"});
                int titleIndex = c.getColumnIndex("title");
                int listingId = c.getColumnIndex("id");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int sellerIndex = c.getColumnIndex("seller");
                int buyerIndex = c.getColumnIndex("buyer");
                int priceIndex = c.getColumnIndex("price");
                int availableIndex = c.getColumnIndex("available");
                c.moveToFirst();
                while(!c.isAfterLast()){
                    String id = c.getString(listingId);
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String user = c.getString(sellerIndex);
                    String price = c.getString(priceIndex);
                    String available = c.getString(availableIndex);
                    String buyer = c.getString(buyerIndex);

                    Tickets ticket = new Tickets(title,id,date,price,loc,user,buyer,available);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }
            return listings;
        }
        return listings;
    }

    public ArrayList<Offer> getOffers(String username, String listingId){
        createTable();
        ArrayList<Offer> offers = new ArrayList<>();
        if (username != null) {
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM offers WHERE buyerUsername LIKE ?",
                    new String[]{"%" + username + "%"});
            int idIndex = c.getColumnIndex("id");
            int offerAmtIndex = c.getColumnIndex("offerAmount");
            int statusIndex = c.getColumnIndex("status");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String id = c.getString(idIndex);
                String offerAmt = c.getString(offerAmtIndex);
                String status = c.getString(statusIndex);

                Offer offer = new Offer(id,offerAmt,username,status);
                offers.add(offer);
                c.moveToNext();
            }
            c.close();
            return offers;
        } else {
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM offers WHERE id LIKE ?",
                    new String[]{"%" + listingId + "%"});
            int idIndex = c.getColumnIndex("id");
            int offerAmtIndex = c.getColumnIndex("offerAmount");
            int userIndex = c.getColumnIndex("buyerUsername");
            int statusIndex = c.getColumnIndex("status");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String id = c.getString(idIndex);
                String offerAmt = c.getString(offerAmtIndex);
                String status = c.getString(statusIndex);
                String buyer = c.getString(userIndex);

                Offer offer = new Offer(id,offerAmt,buyer,status);
                offers.add(offer);
                c.moveToNext();
            }
            c.close();
            return offers;
        }
    }

    /**
     * Returns college information given the name of the college
     * @param name - name of college
     * @return college item of colleges to be able to access name, lat, long
     */
    public Colleges getCollege(String name){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM colleges WHERE college LIKE ?",
                new String[]{"%" + name + "%"});
        int latIndex = c.getColumnIndex("latitude");
        int lonIndex = c.getColumnIndex("longitude");
        c.moveToFirst();
        String latitude = c.getString(latIndex);
        String longitude = c.getString(lonIndex);

        Colleges college = new Colleges(name,latitude,longitude);

        c.close();
        return college;
    }

    public ArrayList<Colleges> getAllColleges(){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM colleges",null);
        int nameIndex = c.getColumnIndex("college");
        int latIndex = c.getColumnIndex("latitude");
        int lonIndex = c.getColumnIndex("longitude");

        c.moveToFirst();
        ArrayList<Colleges> colleges = new ArrayList<>();
        while(!c.isAfterLast()) {
            String name = c.getString(nameIndex);
            String latitude = c.getString(latIndex);
            String longitude = c.getString(lonIndex);

            Colleges college = new Colleges(name, latitude, longitude);
            colleges.add(college);
            c.moveToNext();
        }

        c.close();
        return colleges;
    }
}
