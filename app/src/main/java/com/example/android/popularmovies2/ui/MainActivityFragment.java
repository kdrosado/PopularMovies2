package com.example.android.popularmovies2.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.adapters.GridAdapter;
import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.data_objects.Movie;

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
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "MainActivityFragment";

    private static final String[] FAV_COLUMNS = {
            MovieContract.FavEntry._ID,
            MovieContract.FavEntry.COLUMN_MOVIE_ID,
            MovieContract.FavEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavEntry.COLUMN_POSTER_PATH,
            MovieContract.FavEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavEntry.COLUMN_PLOT
    };

    //grid view for images
    private GridView mGridView;
    private GridAdapter mGridAdapter;

    private ArrayList<Movie> mMovies = null;

    private static final String CHOICE_SETTING_KEY = "choice";
    private static final String MOVIES_DATA_KEY = "movies";
    private static final String MOST_POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";

    private String mChoice = MOST_POPULAR;

    public MainActivityFragment() {
    }

    /**
     *call back interface for all activities that contains this Fragment.
     * Activities should implement this callback to get notified for item selections
     */
    public interface Callback {
        void onItemSelected(Movie movie);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        Log.d(LOG_TAG,"Options main Menu created");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_mostpopular:
                Log.d(LOG_TAG,"most popular selected");
                mChoice = MOST_POPULAR;
                updateMovies(mChoice);
                Toast.makeText(getActivity(), "Most Popular Movies", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_mostRated:
                Log.d(LOG_TAG,"most rated selected");
                mChoice = TOP_RATED;
                updateMovies(mChoice);
                Toast.makeText(getActivity(), "Highest Rated Movies", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_favorite:
                Log.d(LOG_TAG,"fav selected");
                mChoice = FAVORITE;
                updateMovies(mChoice);
                Toast.makeText(getActivity(), "Favorite Movies", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //create a view and inflate main fragment into it
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //set gridview into this view
        mGridView = (GridView)view.findViewById(R.id.gridview_movies);

        mGridAdapter = new GridAdapter(getActivity(), new ArrayList<Movie>());
        //set custom adaptor into view
        mGridView.setAdapter(mGridAdapter);

        //set click listener into view
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mGridAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CHOICE_SETTING_KEY)) {
                mChoice = savedInstanceState.getString(CHOICE_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_DATA_KEY);
                mGridAdapter.setData(mMovies);
            } else {
                updateMovies(mChoice);
            }
        } else {
            updateMovies(mChoice);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mChoice.contentEquals(MOST_POPULAR)) {
            outState.putString(CHOICE_SETTING_KEY, mChoice);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_DATA_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateMovies(String choice) {
        //if we want other then favorite movies
        if (!choice.contentEquals(FAVORITE)) {
            new FetchMovies().execute(choice);
        } else {
            new FetchFav(getActivity()).execute();
        }
    }

    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = "Fetch Movies";

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonResponseString = null;

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG,"Fetch movies started");
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0){
                Log.d(LOG_TAG, "Died - total Params length is 0");
                return null;
            }

            try {
                String choice = params[0];

                URL url = new URL("http://api.themoviedb.org/3/movie/" + choice + "?api_key="
                        + getString(R.string.API_KEY));

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                //added new line for pretty printing
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                jsonResponseString = buffer.toString();
                //Log.d(LOG_TAG,"Result :" + jsonResponseString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, "Error "+ e);
                    }
                }
            }

            try {
                return getMoviesFromJson(jsonResponseString);
            } catch (JSONException e) {
                Log.d(LOG_TAG,"Error " + e);
            }

            //if we failed everywhere this will be returned
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            //we got movies so let's show them
            //puts movies into adaptor
            if (movies != null) {
                if (mGridAdapter != null) {
                    mGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
            Log.d(LOG_TAG,"Post execute of Fetch Movies");
        }

        private List<Movie> getMoviesFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movie);
                results.add(movieModel);
            }

            //Log.d(LOG_TAG,results.toString());
            return results;
        }
    }

    public class FetchFav extends AsyncTask<String, Void, List<Movie>> {

        private Context mContext;

        //constructor
        public FetchFav(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.FavEntry.CONTENT_URI,
                    FAV_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            //we got Fav movies so let's show them
            if (movies != null) {
                if (mGridAdapter != null) {
                    mGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }

        private List<Movie> getFavMoviesFromCursor(Cursor cursor) {
            List<Movie> results = new ArrayList<>();
            //if we have data in database for Fav. movies.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }
}

