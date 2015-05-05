package com.example.amado.traveltracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amado on 04/05/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="traveltracker.db";
    private static final int DATABASE_VERSION =  1;



    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);



    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE memories(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "notes TEXT," +
                "city TEXT," +
                "country TEXT," +
                "latitude DOUBLE," +
                "longitude DOUBLE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
