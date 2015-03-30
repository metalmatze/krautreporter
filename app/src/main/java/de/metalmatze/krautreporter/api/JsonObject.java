package de.metalmatze.krautreporter.api;

import com.google.gson.annotations.Expose;

public class JsonObject<T> {

    @Expose
    public T data;

}
