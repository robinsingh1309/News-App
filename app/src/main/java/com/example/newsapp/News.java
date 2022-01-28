package com.example.newsapp;

public class News {
    private String sectionName;
    private String webPublicationDate;
    private String headline;
    private String thumbnail;
    private String webTitle;
    private String webUrl;

    public News(String sectionName, String webPublicationDate, String headline, String thumbnail, String webTitle, String webUrl) {
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.headline = headline;
        this.thumbnail = thumbnail;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }
}