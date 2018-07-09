package com.example.android.popularmovies2;

import android.content.Context;
import android.database.Cursor;

import com.example.android.popularmovies2.data.MovieContract;

public class Utility {

    //takes movie_id and tells whether or not that movie is favored
    public static int isFavored(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.FavEntry.CONTENT_URI,
                null,   // projection
                MovieContract.FavEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildPosterUrl(String PosterPath) {
        //use recommended w185 size for image
        return "http://image.tmdb.org/t/p/w185" + PosterPath;
    }

}