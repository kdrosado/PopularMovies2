package com.example.android.popularmovies2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data_objects.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

//import com.squareup.picasso.Picasso;

public class TrailerAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final Trailer mTrailer = new Trailer();
    private List<Trailer> mTrailerObjects;

    //constructor
    public TrailerAdapter(Context context, List<Trailer> TrailerObjects) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTrailerObjects = TrailerObjects;
    }

    //Getter method
    public Context getmContext(){
        return mContext;
    }

    public void add(Trailer object){
        synchronized (mTrailer){
            mTrailerObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (mTrailer){
            mTrailerObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTrailerObjects.size();
    }

    @Override
    public Trailer getItem(int position) {
        return mTrailerObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TrailerViewHolder viewHolder;

        if (view == null){
            view = mLayoutInflater.inflate(R.layout.movie_trailer, parent, false);
            viewHolder = new TrailerViewHolder(view);
            view.setTag(viewHolder);
        }

        final Trailer trailer = getItem(position);
        viewHolder = (TrailerViewHolder) view.getTag();

        String trailer_thumb_url = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        //load trailer thumbnail into image view with picasso
        Picasso.with(getmContext()).load(trailer_thumb_url).into(viewHolder.imageView);
        //set trailer name (title)
        viewHolder.nameView.setText(trailer.getName());

        return view;
    }

    public static class TrailerViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public TrailerViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
            nameView = (TextView) view.findViewById(R.id.trailer_name);
        }
    }
}
