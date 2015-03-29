package de.metalmatze.krautreporter.models;

import io.realm.RealmObject;

public class Image extends RealmObject {

    private int id;

    private int width;

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
