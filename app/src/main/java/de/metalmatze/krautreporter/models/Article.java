package de.metalmatze.krautreporter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.metalmatze.krautreporter.api.JsonArray;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {

    @PrimaryKey
    @Expose
    private int id;

    @Expose
    private int order;

    @Expose
    private String title;

    @Expose
    private String headline;

    @Expose
    private Date date;

    @Expose
    private boolean morgenpost;

    @Expose
    private boolean preview;

    @Index
    @Expose
    private String url;

    @Expose
    private String excerpt;

    @Expose
    private String content;

    @Expose
    private Date createdAt;

    @Expose
    private Date updatedAt;

    private RealmList<Image> images;

    private Author author;

    @Ignore
    @Expose
    @SerializedName("author_id")
    private int serializedAuthor;

    @Ignore
    @Expose
    @SerializedName("images")
    private JsonArray<Image> serializedImages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isMorgenpost() {
        return morgenpost;
    }

    public void setMorgenpost(boolean morgenpost) {
        this.morgenpost = morgenpost;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getSerializedAuthor() {
        return serializedAuthor;
    }

    public void setSerializedAuthor(int serializedAuthor) {
        this.serializedAuthor = serializedAuthor;
    }

    public JsonArray<Image> getSerializedImages() {
        return serializedImages;
    }

    public void setSerializedImages(JsonArray<Image> serializedImages) {
        this.serializedImages = serializedImages;
    }
}
