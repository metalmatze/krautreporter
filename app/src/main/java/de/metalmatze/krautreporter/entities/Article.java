package de.metalmatze.krautreporter.entities;

public class Article {

    private final String date;
    private final String headline;

    public Article(String date, String headline) {
        this.date = date;
        this.headline = headline;
    }

    public String getDate() {
        return date;
    }

    public String getHeadline() {
        return headline;
    }
}
