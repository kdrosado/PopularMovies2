package com.example.android.popularmovies2.data_objects;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class Movie implements Parcelable {

    private int movie_id; //movie id
    private String movie_title; // original_title
    private String release_date; // release_date
    private String poster_path; // poster_path
    private String vote_avg; // vote_average
    private String plot; //plot

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_POSTER_PATH = 4;
    public static final int COL_VOTE_AVERAGE = 5;
    public static final int COL_PLOT = 6;

    //movie() is overloaded
    public Movie() {
    }

    //when we get data from api for popular and top rated movies we will pass JSONObject
    public Movie(JSONObject movie) throws JSONException {
        this.movie_id = movie.getInt("id");
        this.movie_title = movie.getString("original_title");
        this.release_date = movie.getString("release_date");
        this.poster_path = movie.getString("poster_path");
        this.vote_avg = movie.getString("vote_average");
        this.plot = movie.getString("overview");

    }

    //when we ask for favorite movies we will do that via Cursor
    public Movie(Cursor cursor) {
        this.movie_id = cursor.getInt(COL_MOVIE_ID);
        this.movie_title = cursor.getString(COL_MOVIE_TITLE);
        this.release_date = cursor.getString(COL_RELEASE_DATE);
        this.poster_path = cursor.getString(COL_POSTER_PATH);
        this.vote_avg = cursor.getString(COL_VOTE_AVERAGE);
        this.plot = cursor.getString(COL_PLOT);

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(movie_title);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(vote_avg);
        dest.writeString(plot);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        movie_id = in.readInt();
        movie_title = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_avg = in.readString();
        plot = in.readString();
    }

    //Getter methods
    public int getMovie_id() {
        return movie_id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getPlot() {
        return plot;
    }

    public String getVote_avg() {
        return vote_avg;
    }

    public String getRelease_date() {
        return release_date;
    }

}
