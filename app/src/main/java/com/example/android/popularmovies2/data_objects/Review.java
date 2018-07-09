package com.example.android.popularmovies2.data_objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private String review_id;
    private String review_author;
    private String review_content;

    public Review() {
    }

    public Review(JSONObject review) throws JSONException {
        this.review_id = review.getString("id");
        this.review_author = review.getString("author");
        this.review_content = review.getString("content");
    }

    //Getter methods
    public String getReview_id() {
        return review_id;
    }

    public String getReview_author() {
        return review_author;
    }

    public String getReview_content() {
        return review_content;
    }
}
