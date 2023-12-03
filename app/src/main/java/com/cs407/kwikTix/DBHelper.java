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
                "(id INTEGER, title TEXT,date TEXT,price TEXT, college TEXT,username TEXT, FOREIGN KEY(college) REFERENCES colleges(college), FOREIGN KEY(username) REFERENCES users(username), PRIMARY KEY (username,id))");
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
     * Adds ticket to listings db
     * @param title
     * @param date
     * @param college
     * @param username
     */
    public void addTicket(String title,String date,String price,String college, String username){
        createTable();
        try {
            sqLiteDatabase.execSQL("INSERT INTO listings (title, date, price, college, username) VALUES (?,?,?,?,?)",
                    new String[]{title,date,price,college,username});
            Log.i("Yay", "Ticket created");
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message) TODO: Same title
            Log.i("Info Listings(Primary Key)", "Same primary key for " + title);
        }
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
        createTable();
        Notes note = new Notes(date,username,title,content);
        sqLiteDatabase.execSQL("UPDATE notes set content=?, date=? where title=? and username=?",
                new String[]{content,date,title,username});
    }


 */
// String strSQL = "UPDATE myTable SET Column1 = someValue WHERE columnId = "+ someValue;

    /**
     * Gets listings for a specific user
     * @param username
     * @return
     */
    public ArrayList<Tickets> getListings(String username, String college, String sort_by, boolean desc){
        createTable();
        ArrayList<Tickets> listings = new ArrayList<>();
        if (username == null && college == null && sort_by == null){
            //returns all listings
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings", null);
            int titleIndex = c.getColumnIndex("title");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int usernameIndex = c.getColumnIndex("username");
            int priceIndex = c.getColumnIndex("price");
            c.moveToFirst();
            while(!c.isAfterLast()){
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String user = c.getString(usernameIndex);
                String price = c.getString(priceIndex);

                Tickets ticket = new Tickets(title,date,price,loc,user);
                listings.add(ticket);
                c.moveToNext();
            }
            c.close();
            return listings;
        }else if (username != null){
            //returns listings for username
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE username LIKE ?",
                    new String[]{"%" + username + "%"});
            int titleIndex = c.getColumnIndex("title");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int priceIndex = c.getColumnIndex("price");
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String price = c.getString(priceIndex);
                Tickets ticket = new Tickets(title, date, price, loc, username);
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
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int priceIndex = c.getColumnIndex("price");
            int usernameIndex = c.getColumnIndex("username");
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String price = c.getString(priceIndex);
                String user = c.getString(usernameIndex);
                Tickets ticket = new Tickets(title, date, price, loc, user);
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
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int priceIndex = c.getColumnIndex("price");
                int usernameIndex = c.getColumnIndex("username");
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String price = c.getString(priceIndex);
                    String user = c.getString(usernameIndex);
                    Tickets ticket = new Tickets(title, date, price, loc, user);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings ORDER BY cast(price as numeric) ASC", null);
                int titleIndex = c.getColumnIndex("title");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int priceIndex = c.getColumnIndex("price");
                int usernameIndex = c.getColumnIndex("username");
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String price = c.getString(priceIndex);
                    String user = c.getString(usernameIndex);
                    Tickets ticket = new Tickets(title, date, price, loc, user);
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
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int priceIndex = c.getColumnIndex("price");
                int usernameIndex = c.getColumnIndex("username");
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String price = c.getString(priceIndex);
                    String user = c.getString(usernameIndex);
                    Tickets ticket = new Tickets(title, date, price, loc, user);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            } else {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY cast(price as numeric) ASC",
                        new String[]{"%" + college + "%"});
                int titleIndex = c.getColumnIndex("title");
                int dateIndex = c.getColumnIndex("date");
                int collegeIndex = c.getColumnIndex("college");
                int priceIndex = c.getColumnIndex("price");
                int usernameIndex = c.getColumnIndex("username");
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String title = c.getString(titleIndex);
                    String date = c.getString(dateIndex);
                    String loc = c.getString(collegeIndex);
                    String price = c.getString(priceIndex);
                    String user = c.getString(usernameIndex);
                    Tickets ticket = new Tickets(title, date, price, loc, user);
                    listings.add(ticket);
                    c.moveToNext();
                }
                c.close();
            }
            return listings;
        }
        // TODO: IMPLEMENT sort by date
        /*else if (college == null && sort_by.equals("date")){
            //filter and sort
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE college LIKE ? ORDER BY cast(price as numeric) ASC",
                    new String[]{"%" + college + "%"});
            int titleIndex = c.getColumnIndex("title");
            int dateIndex = c.getColumnIndex("date");
            int collegeIndex = c.getColumnIndex("college");
            int priceIndex = c.getColumnIndex("price");
            int usernameIndex = c.getColumnIndex("username");
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String title = c.getString(titleIndex);
                String date = c.getString(dateIndex);
                String loc = c.getString(collegeIndex);
                String price = c.getString(priceIndex);
                String user = c.getString(usernameIndex);
                Tickets ticket = new Tickets(title, date, price, loc, user);
                listings.add(ticket);
                c.moveToNext();
            }
            c.close();
        }*/
        return listings;
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
