package de.metalmatze.krautreporter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.net.URL;
import java.util.Date;

@Table(name = "articles")
public class ArticleModel extends Model {

    @Column(name = "uuid", index = true, unique = true)
    public String uuid;

    @Column(name = "title")
    public String title;

    @Column(name = "date")
    public Date date;

    @Column(name = "link")
    public URL link;

    @Column(name = "image")
    public URL image;

    @Column(name = "content")
    public String content;

}
