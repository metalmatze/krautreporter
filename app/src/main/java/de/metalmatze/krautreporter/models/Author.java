package de.metalmatze.krautreporter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.metalmatze.krautreporter.api.JsonArray;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Author extends RealmObject {

    @PrimaryKey
    @Expose
    private int id;

    @Expose
    private String name;

    @Expose
    private String title;

    @Expose
    private String url;

    @Expose
    private String biography;

    @Expose
    private String socialmedia;

    @Expose
    private String created_at;

    @Expose
    private String updated_at;

    private RealmList<Image> images;

    private RealmList<Article> articles;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getSocialmedia() {
        return socialmedia;
    }

    public void setSocialmedia(String socialmedia) {
        this.socialmedia = socialmedia;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public RealmList<Article> getArticles() {
        return articles;
    }

    public void setArticles(RealmList<Article> articles) {
        this.articles = articles;
    }

    public JsonArray<Image> getSerializedImages() {
        return serializedImages;
    }

    public void setSerializedImages(JsonArray<Image> serializedImages) {
        this.serializedImages = serializedImages;
    }
}
