package com.example.android.popularmovies2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

// inspired from sunshine code

public class MovieContract {
    //it should be unique in system,we use package name because it is unique
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies2";

    //base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //URI end points for Content provider
    public static final String PATH_FAV = "fav";


    //for favorites
    public static final class FavEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        //these are MIME types ,not really but they are similar to MIME types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        // Table name
        public static final String TABLE_NAME = "fav";
        //TMDB Movie id ; we will need this reviews and Trailer
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Movie title
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        //Movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";
        //path for poster ; it's not actual URL, append it with base poster path with size. example
        // http://image.tmdb.org/t/p/{size}/{poster_path}
        public static final String COLUMN_POSTER_PATH = "poster_path";
        //vote average for Movie
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        //plot synopsis of Movie
        public static final String COLUMN_PLOT = "plot";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
