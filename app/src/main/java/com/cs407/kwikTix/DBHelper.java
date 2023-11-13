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

    public static void createTable(){
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS colleges "+
                "(college TEXT PRIMARY KEY,latitude TEXT, longitude TEXT)");
    }
    public ArrayList<Colleges> getColleges(String name){
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM colleges WHERE college LIKE ?",
                new String[]{"%" + name + "%"});
        int latIndex = c.getColumnIndex("latitude");
        int lonIndex = c.getColumnIndex("longitude");
        c.moveToFirst();
        ArrayList<Colleges> colleges = new ArrayList<>();
        while(!c.isAfterLast()){
            String latitude = c.getString(latIndex);
            String longitude = c.getString(lonIndex);

            Colleges college = new Colleges(name,latitude,longitude);
            colleges.add(college);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();
        return colleges;
    }
    public void addCollege(String college,String latitude,String longitude){
        createTable();
        try {
            sqLiteDatabase.execSQL("INSERT INTO colleges (college, latitude, longitude) VALUES (?,?,?)",
                    new String[]{college,latitude,longitude});
        } catch (SQLiteConstraintException e) {
            // Handle the exception (e.g., log it or show a message)
            Log.i("Info(Primary Key)", "Same primary key for " + college);
        }
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
