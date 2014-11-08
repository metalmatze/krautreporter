package de.metalmatze.krautreporter.entities;

import java.net.URL;
import java.util.Date;

public class Article {

    private String uuid;
    private String title;
    private Date date;
    private URL link;
    private String content;
    private URL teaserImage;

    public Article() {
    }

    public Article(String uuid, String title, Date date, URL link, String content, URL teaserImage) {
        this.uuid = uuid;
        this.title = title;
        this.date = date;
        this.link = link;
        this.content = content;
        this.teaserImage = teaserImage;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URL getTeaserImage() {
        return teaserImage;
    }

    public void setTeaserImage(URL teaserImage) {
        this.teaserImage = teaserImage;
    }

    @Override
    public String toString() {
        return "Article{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", link=" + link +
                ", teaserImage=" + teaserImage +
                ", content='" + content + '\'' +
                '}';
    }
}
