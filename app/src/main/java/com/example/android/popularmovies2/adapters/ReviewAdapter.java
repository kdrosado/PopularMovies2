package com.example.android.popularmovies2.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data_objects.Review;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final Review mReview = new Review();

    private final List<Review> mReviewObjects;

    //constructor
    public ReviewAdapter(Context context, List<Review> ReviewObjects) {
        mContext = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mReviewObjects = ReviewObjects;
    }

    //getter method
    public Context getmContext(){
        return mContext;
    }

    public void add(Review object){
        synchronized (mReview){
            mReviewObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (mReview){
            mReviewObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mReviewObjects.size();
    }

    @Override
    public Review getItem(int position) {
        return mReviewObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ReviewViewHolder viewHolder;
        if (view == null){
            view = mLayoutInflater.inflate(R.layout.movie_review, parent, false);
            viewHolder = new ReviewViewHolder(view);
            view.setTag(viewHolder);
        }

        final Review review = getItem(position);
        viewHolder = (ReviewViewHolder) view.getTag();

        viewHolder.authorView.setText(review.getReview_author());
        viewHolder.contentView.setText(Html.fromHtml(review.getReview_content()));

        return view;
    }

    public static class ReviewViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ReviewViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }
}