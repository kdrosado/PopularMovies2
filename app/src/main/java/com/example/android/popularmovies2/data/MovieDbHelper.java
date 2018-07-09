package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "MovieDbHelper";
    // If you change the database schema, you must increment the database version
    // inspired from sunshine
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create database tables here
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + MovieContract.FavEntry.TABLE_NAME + " ( " +
                MovieContract.FavEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.FavEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.FavEntry.COLUMN_PLOT + " TEXT NOT NULL " +
                ");";

        //gotta do logging
        Log.d(LOG_TAG,SQL_CREATE_FAV_TABLE);

        db.execSQL(SQL_CREATE_FAV_TABLE);

        Log.d(LOG_TAG,"all tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this will be invoked when we change DATABASE_VERSION
        // if we upgrade schema user will lose favorite collection
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavEntry.TABLE_NAME);
        onCreate(db);
    }
}
