package com.example.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */
public class QueryUtils {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Creating a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<News> fetchNewsData(String mUrl) {
        URL url = createURL(mUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the Http request: ", e);
        }
        return extractNews(jsonResponse);
    }

    // creating the url
    private static URL createURL(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL: ", e);
        }
        return url;
    }

    // this method helps in connection with the server and after establishing connection
    // it will request the server for retrieval of information in the form of json format
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error in Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in parsing json Response: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // it will convert input stream bytes into character stream tokens
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                // it will read next line
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<News> extractNews(String jsonResponse) {

        // Checking if jsonResponse is empty, if empty then we will simply return null
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Creating an empty ArrayList so that we can start adding news to it.
        List<News> news = new ArrayList<>();
        // Trying to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catching the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the jsonResponse string and
            // build up a list of News objects with the corresponding data.
            JSONObject baseJsonRoot = new JSONObject(jsonResponse);
            JSONObject response = baseJsonRoot.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentObject = results.getJSONObject(i);
                String sectionName = currentObject.getString("sectionName");
                String webPublicationDate = currentObject.getString("webPublicationDate");
                String headline = currentObject.getString("webTitle");
                String webUrl = currentObject.getString("webUrl");

                JSONObject fields = currentObject.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");

                JSONObject currentTagObject;
                String webTitle = null;
                JSONArray tags = currentObject.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++) {
                    currentTagObject = tags.getJSONObject(j);
                    webTitle = currentTagObject.getString("webTitle");
                }
                news.add(new News(sectionName, webPublicationDate, headline, thumbnail, webTitle, webUrl));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON result: ", e);
        }
        return news;
    }
}
