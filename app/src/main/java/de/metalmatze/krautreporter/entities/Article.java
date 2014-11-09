package de.metalmatze.krautreporter.entities;

import java.net.URL;
import java.util.Date;

public class Article {

    private String uuid;
    private String title;
    private Date date;
    private URL link;
    private URL image;
    private String content;

    public Article() {
    }

    public Article(String uuid, String title, Date date, URL link, URL image, String content) {
        this.uuid = uuid;
        this.title = title;
        this.date = date;
        this.link = link;
        this.image = image;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
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
                ", image=" + image +
//                ", content='" + content + '\'' +
                '}';
    }
}
