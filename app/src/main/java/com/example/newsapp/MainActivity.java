package com.example.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * URL for News data
     */
    private static final String GUARDIAN_API = "https://content.guardianapis.com/search";
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if we're using multiple loaders.
     */
    private static final int GUARDIAN_API_LOADER_ID = 1;
    // Initialising the customised adapter globally
    private NewsAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the TextView
        emptyTextView = (TextView) findViewById(R.id.empty_view);

        // Find a reference to the {@link ListView} in the layout
        ListView listView = (ListView) findViewById(R.id.list);

        /** this @setEmptyView method will handle multiple cases
         *  1. if there is no data fetched from server it will display "no news data found" as text to the user
         *  2. if there is no internet connection it will set the text to display user that they're "not connected to Internet"
         *  3. if data is fetched successfully then it will not display any text
         */
        listView.setEmptyView(emptyTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(mAdapter);

        // if user want to read full article then they can click on the list
        // and it will open the article in the appropriate browser
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = mAdapter.getItem(position);
                Uri uri = Uri.parse(news.getWebUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(websiteIntent);
            }
        });

        // It checks for the connection if data is ON/OFF and checks if connection is in Airplane mode or not
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // If there is connection then we will call the loader to fetch the details
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GUARDIAN_API_LOADER_ID, null, this);
        } else {
            // this will display the desired text when there is no connection to the user
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minPageSize = sharedPreferences.getString(
                getString(R.string.settings_min_page_size_key),
                getString(R.string.settings_min_page_size_key_default));

        // Passing the @link{GUARDIAN_API}
        Uri baseUri = Uri.parse(GUARDIAN_API);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // appending different queries
        uriBuilder.appendQueryParameter(getString(R.string.format), getString(R.string.json));
        uriBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.contributor));
        uriBuilder.appendQueryParameter(getString(R.string.page_size), minPageSize);
        uriBuilder.appendQueryParameter(getString(R.string.show_fields), getString(R.string.thumbnail));
        uriBuilder.appendQueryParameter(getString(R.string.order_date), getString(R.string.last_modified));
        uriBuilder.appendQueryParameter(getString(R.string.order_by), getString(R.string.newest));
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_value));

        // finally after appending query, passing the desired url as a parameter to
        // @link{NewsLoader} to fetch the results from the server
        // creating the loader instance
        // and this will return the list of news
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // As soon as data is fetched successfully from the server
        // the Progress bar will vanish and it will update the UI
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No News to Display"
        emptyTextView.setText(R.string.no_news);

        // Clearing the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}