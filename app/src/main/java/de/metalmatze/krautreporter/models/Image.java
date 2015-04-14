package de.metalmatze.krautreporter.models;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Image extends RealmObject {

    @PrimaryKey
    @Expose
    private int id;

    @Expose
    private int width;

    @Expose
    private String src;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
