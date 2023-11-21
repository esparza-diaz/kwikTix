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
                "(title TEXT PRIMARY KEY,date TEXT,price TEXT, college TEXT,username TEXT, FOREIGN KEY(college) REFERENCES colleges(college), FOREIGN KEY(username) REFERENCES users(username))");
    }

    /**
     * Adds user to users db. Should be called after user registers
     * @param username
     * @param password
     * @param email
     * @param phone
     * @param prefContactMethod
     * @param college
     */
    public void addUser(String username,String password,String email, String phone, String prefContactMethod, String college){
        createTable();
        try {
            sqLiteDatabase.execSQL("INSERT INTO users (username, password, email, phone, prefContactMethod, college) VALUES (?,?,?,?,?,?)",
                    new String[]{username, password, email, phone, prefContactMethod, college});
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message) TODO: Logic to catch for same user
            Log.i("Info User(Primary Key)", "Same primary key for " + username);
        }
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
    public Users getUser(String username){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE username LIKE ?",
                new String[]{"%" + username + "%"});
        int passwordIndex = c.getColumnIndex("password");
        int emailIndex = c.getColumnIndex("email");
        int phoneIndex = c.getColumnIndex("phone");
        int prefContactMethodIndex = c.getColumnIndex("prefContactMethod");
        int collegeIndex = c.getColumnIndex("college");
        c.moveToFirst();
        String password = c.getString(passwordIndex);
        String email = c.getString(emailIndex);
        String phone = c.getString(phoneIndex);
        String prefContactMethod = c.getString(prefContactMethodIndex);
        String college = c.getString(collegeIndex);

        Users user = new Users(username, password, email, phone, prefContactMethod, college);

        c.close();
        return user;
    }

    /**
     * Gets listings for a specific user
     * @param username
     * @return
     */
    public ArrayList<Tickets> getListings(String username){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings WHERE username LIKE ?",
                new String[]{"%" + username + "%"});
        int titleIndex = c.getColumnIndex("title");
        int dateIndex = c.getColumnIndex("date");
        int collegeIndex = c.getColumnIndex("college");
        int priceIndex = c.getColumnIndex("price");
        c.moveToFirst();
        ArrayList<Tickets> listings = new ArrayList<>();
        while(!c.isAfterLast()){
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String college = c.getString(collegeIndex);
            String price = c.getString(priceIndex);
            Tickets ticket = new Tickets(title,date,price,college,username);
            listings.add(ticket);
            c.moveToNext();
        }

        c.close();
        return listings;
    }

    /**
     *
     * @return
     */
    public ArrayList<Tickets> getListings(){
        Log.i("Yay", "Getting al Listings");
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM listings", null);
        int titleIndex = c.getColumnIndex("title");
        int dateIndex = c.getColumnIndex("date");
        int collegeIndex = c.getColumnIndex("college");
        int usernameIndex = c.getColumnIndex("username");
        int priceIndex = c.getColumnIndex("price");
        c.moveToFirst();
        ArrayList<Tickets> listings = new ArrayList<>();
        while(!c.isAfterLast()){
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String college = c.getString(collegeIndex);
            String username = c.getString(usernameIndex);
            String price = c.getString(priceIndex);

            Tickets ticket = new Tickets(title,date,price,college,username);
            listings.add(ticket);
            c.moveToNext();
        }

        c.close();
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
    /*
    public void updateNotes(String content,String date,String title,String username){
        createTable();
        Notes note = new Notes(date,username,title,content);
        sqLiteDatabase.execSQL("UPDATE notes set content=?, date=? where title=? and username=?",
                new String[]{content,date,title,username});
    }

    public void deleteNotes(String content,String title){
        createTable();
        String date = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT date FROM notes WHERE content=?",
                new String[]{content});
        if(cursor.moveToNext()){
            date = cursor.getString(0);
        }
        sqLiteDatabase.execSQL("DELETE FROM notes WHERE content=? AND date=?",
                new String[]{content,date});
        cursor.close();
    }
 */
}
