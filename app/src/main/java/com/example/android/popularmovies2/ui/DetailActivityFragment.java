package com.example.android.popularmovies2.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView.OnItemClickListener;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.Utility;
import com.example.android.popularmovies2.adapters.ReviewAdapter;
import com.example.android.popularmovies2.adapters.TrailerAdapter;
import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.data_objects.Movie;
import com.example.android.popularmovies2.data_objects.Review;
import com.example.android.popularmovies2.data_objects.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    //TAG for Fragment manager
    public static final String TAG = "DetailActivityFragment";

    private static final String LOG_TAG = "DetailActivityFragment";

    static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private Movie mMovie;

    //All the Views for detail Fragment
    private ImageView mPosterView;

    private TextView mMovieTitleView;
    private TextView mPlotView;
    private TextView mReleaseDateView;
    private TextView mVoteAverageView;

    private LinearListView mTrailersView;
    private LinearListView mReviewsView;

    private CardView mReviewsCardview;
    private CardView mTrailersCardview;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private Toast mToast;

    private ScrollView mDetailLayout;

    private Trailer mTrailer;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovie != null) {
            inflater.inflate(R.menu.menu_detail, menu);
            Log.d(LOG_TAG, "detail Menu created");

            final MenuItem action_fav = menu.findItem(R.id.action_fav);
            MenuItem action_share = menu.findItem(R.id.action_share);

            //set proper icon on toolbar for favored movies
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.isFavored(getActivity(), mMovie.getMovie_id());
                }

                @Override
                protected void onPostExecute(Integer isFavored) {
                    action_fav.setIcon(isFavored == 1 ?
                            R.drawable.ic_favorite_black_24dp :
                            R.drawable.ic_favorite_border_black_24dp);
                }
            }.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_fav:
                if (mMovie != null) {
                    // check if movie is favored or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.isFavored(getActivity(), mMovie.getMovie_id());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavored) {
                            // if it is in favorites
                            if (isFavored == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.FavEntry.CONTENT_URI,
                                                MovieContract.FavEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mMovie.getMovie_id())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_ID, mMovie.getMovie_id());
                                        values.put(MovieContract.FavEntry.COLUMN_MOVIE_TITLE, mMovie.getMovie_title());
                                        values.put(MovieContract.FavEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date());
                                        values.put(MovieContract.FavEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());
                                        values.put(MovieContract.FavEntry.COLUMN_VOTE_AVERAGE, mMovie.getVote_avg());
                                        values.put(MovieContract.FavEntry.COLUMN_PLOT, mMovie.getPlot());

                                        return getActivity().getContentResolver().insert(MovieContract.FavEntry.CONTENT_URI, values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;

            case R.id.action_share:
                //share movie trailer
                shareTrailer();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovie = arguments.getParcelable(DETAIL_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDetailLayout = (ScrollView) rootView.findViewById(R.id.detail_layout);

        if (mMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        mPosterView = (ImageView) rootView.findViewById(R.id.detail_image);
        mMovieTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_overview);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mVoteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);

        mTrailersView = (LinearListView) rootView.findViewById(R.id.detail_trailers);
        mReviewsView = (LinearListView) rootView.findViewById(R.id.detail_reviews);

        mReviewsCardview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        mTrailersCardview = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

        //set our custom trailers adaptor into Trailer's View
        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        mTrailersView.setAdapter(mTrailerAdapter);

        //view Trailer in other app that user has chosen for playing Youtube Videos.
        //Use explicit intent with ACTION_VIEW and URI as Data
        //this will launch user youtube trailer on which user clicked into preferred app
        mTrailersView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {

                Trailer trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }
        });

        //set our custom Review adaptor into Custom reviews view
        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
        mReviewsView.setAdapter(mReviewAdapter);

        if (mMovie != null) {
            String poster_url = Utility.buildPosterUrl(mMovie.getPoster_path());
            //load poster with picasso
            Picasso.with(getContext()).load(poster_url).into(mPosterView);

            mMovieTitleView.setText(mMovie.getMovie_title());
            mPlotView.setText(mMovie.getPlot());
            mReleaseDateView.setText(mMovie.getRelease_date());
            mVoteAverageView.setText(mMovie.getVote_avg());
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovie != null) {
            //fetch trailers with passing movie_id
            new FetchTrailers().execute(Integer.toString(mMovie.getMovie_id()));
            //fetch reviews with passing movie_id
            new FetchReviews().execute(Integer.toString(mMovie.getMovie_id()));
        }
    }

    //created an intent to share first movie trailer from list of trailers
    private void shareTrailer() {

        if (mTrailer != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getMovie_title() + "\n" +
                    "http://www.youtube.com/watch?v=" + mTrailer.getKey()
                    + "\n'Shared Via Popular Movies App, Data is Sourced form http://themoviedb.org/' ");
            sendIntent.setType("text/plain");

            startActivity(Intent.createChooser(sendIntent, "Share Using"));
        } else {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), "Wait for Trailers to Load", Toast.LENGTH_SHORT);
            mToast.show();
        }

    }


    public class FetchReviews extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = "FetchReviews";

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "Fetch reviews Started");
            super.onPreExecute();
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                Log.d(LOG_TAG, "Died - total Params length is 0");
                return null;
            }

            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String jsonResponseString = null;

            try {
                //params[0] is movie_id
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.API_KEY))
                        .build();

                URL url = new URL(builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonResponseString = buffer.toString();

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error " + e);
                return null;

            } finally {
                //do housekeeping stuff, i mean clean up
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "Error " + e);
                    }
                }
            }

            try {
                return getReviewsFromJson(jsonResponseString);
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Error " + e);
            }
            //this will be returned in case we failed everywhere.
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            //we got reviews so let's show them
            if (reviews != null) {
                //if we have reviews
                if (reviews.size() > 0) {
                    mReviewsCardview.setVisibility(View.VISIBLE);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.remove();
                        for (Review review : reviews) {
                            mReviewAdapter.add(review);
                        }
                    }
                }
            }
        }

        private List<Review> getReviewsFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review));
            }

            return results;
        }
    }

    public class FetchTrailers extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = "FetchTrailers";

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "Fetch Trailers started executing");
            super.onPreExecute();
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                Log.d(LOG_TAG, "Died - total params length is 0");
                return null;
            }

            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String jsonResponseString = null;

            try {
                //params[0] is movie_id
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.API_KEY))
                        .build();

                URL url = new URL(builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonResponseString = buffer.toString();

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error " + e);
                return null;
            } finally {
                //do house keeping, i mean clean up
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "Error " + e);
                    }
                }
            }

            try {
                return getTrailersFromJson(jsonResponseString);
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Error " + e);
            }

            //this will be returned in case we failed everywhere.
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            //we got trailers so let's show them
            if (trailers != null) {
                //when we have trailers
                if (trailers.size() > 0) {
                    mTrailersCardview.setVisibility(View.VISIBLE);
                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.remove();
                        for (Trailer trailer : trailers) {
                            mTrailerAdapter.add(trailer);
                        }
                    }

                    //put the first Trailer into mTrailer, this will be used while sharing.
                    //also if this is null means we don't have a Trailer to share while sharing
                    mTrailer = trailers.get(0);
                }
            }
        }

        private List<Trailer> getTrailersFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show Trailers which are on Youtube
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerModel = new Trailer(trailer);
                    results.add(trailerModel);
                }
            }
            //returns a list of trailers that are on youtube
            return results;
        }
    }
}