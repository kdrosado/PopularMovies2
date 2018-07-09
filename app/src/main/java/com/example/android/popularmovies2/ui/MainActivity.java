package com.example.android.popularmovies2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data_objects.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private boolean mTabUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //detect a handle tablet mode with Two pane UI with master detail flow
        if (findViewById(R.id.movie_detail_container) != null) {
            mTabUI = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(),
                                DetailActivityFragment.TAG)
                        .commit();
            }
        } else {
            mTabUI = false;
        }
    }


    // implements callback for Movie Item Click
    //if we have Tablet then update detail fragment into main activity otherwise launch detail
    // activity with an intent
    @Override
    public void onItemSelected(Movie movie) {
        if (mTabUI) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.DETAIL_MOVIE, movie);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailActivityFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivityFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }

}