package de.metalmatze.krautreporter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "articles")
public class ArticleModel extends Model implements Article {

    @Column(name = "uuid", index = true, unique = true)
    private String uuid;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private Date date;

    @Column(name = "link")
    private String link;

    @Column(name = "image")
    private String image;

    @Column(name = "excerpt")
    private String excerpt;

    @Column(name = "content")
    private String content;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ArticleModel{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", excerpt='" + excerpt + '\'' +
                '}';
    }
}
