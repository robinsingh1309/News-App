package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View list_item_view = convertView;
        if (list_item_view == null) {
            // Setting the layout where our objects attributes will be placed
            list_item_view = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        // Find the News at the given position in the list of news
        News newsModel = getItem(position);

        // Initializing the ImageView
        ImageView newsImage = (ImageView) list_item_view.findViewById(R.id.news_image);
        // Initializing the TextView
        TextView newsHeading = (TextView) list_item_view.findViewById(R.id.news_heading);
        // Initializing the TextView
        TextView newsAuthor = (TextView) list_item_view.findViewById(R.id.news_author);
        // Initializing the TextView
        TextView newsDate = (TextView) list_item_view.findViewById(R.id.news_date);
        // Initializing the TextView
        TextView newsContent = (TextView) list_item_view.findViewById(R.id.news_content);

        // Setting the News Heading
        newsHeading.setText(newsModel.getHeadline());
        // Setting the News Author
        newsAuthor.setText(newsModel.getWebTitle());

        // Setting the Date and Time of News
        // We are specifying the date format we are receiving from server
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        // Getting the publication date and storing it in variable for formatting
        String dateInString = newsModel.getWebPublicationDate();
        try {
            // @Z suffix means UTC, java.util.SimpleDateFormat doesn't parse it correctly, we need to replace the suffix Z with ‘+0000’
            Date date = formatter.parse(dateInString.replaceAll("Z$", "+0000"));
            // formatting the date i.e, in Jun 13, 2002 10:30 AM and to get local formatting we are passing @param{Locale.ENGLISH}
            SimpleDateFormat s1 = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
            // checking to see if argument date is null or not
            assert date != null;
            // Setting the date}
            newsDate.setText(s1.format(date));
        } catch (ParseException e) {
            // for error occurring during passing
            e.printStackTrace();
        }
        // Setting the News Content i.e, which section it relates to either science / politics / technology......
        newsContent.setText(newsModel.getSectionName());
        // Setting the News Image with the help of Picasso library
        Picasso.get()
                .load(newsModel.getThumbnail())
                // this will ensure another image if there is no image fetched from url
                .error(R.drawable.image_not_available)
                // setting the image to required @newsImage ImageView
                .into(newsImage);

        // returning the list
        return list_item_view;
    }
}
