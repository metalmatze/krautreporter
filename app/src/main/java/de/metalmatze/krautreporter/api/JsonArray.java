package de.metalmatze.krautreporter.api;

import com.google.gson.annotations.Expose;

import java.util.List;

public class JsonArray<T> {

    @Expose
    public List<T> data;

}
