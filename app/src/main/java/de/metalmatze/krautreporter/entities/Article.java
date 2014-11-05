package de.metalmatze.krautreporter.entities;

import java.net.URL;
import java.util.GregorianCalendar;

public class Article {

    private String uuid;
    private String title;
    private GregorianCalendar date;
    private URL link;
    private String content;

    public Article() {
    }

    public Article(String uuid, String title, GregorianCalendar date, URL link, String content) {
        this.uuid = uuid;
        this.title = title;
        this.date = date;
        this.link = link;
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", link=" + link +
                ", content='" + content + '\'' +
                '}';
    }
}
