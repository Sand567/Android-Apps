package com.sandeep.newsgateway;

import java.io.Serializable;

/**
 * Created by Sandeep on 30-04-2017.
 */

public class Articles implements Serializable {

    private String Author;
    private String title;
    private String description;
    private String url;
    private String publishDate;
    private String websiteUrl;

    public Articles(String author, String title, String description, String url, String publishDate, String websiteUrl) {
        Author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishDate = publishDate;
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
