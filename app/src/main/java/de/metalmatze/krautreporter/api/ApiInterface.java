package de.metalmatze.krautreporter.api;

import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ApiInterface {

    @GET("/articles")
    void articles(Callback<JsonArray<Article>> callback);

    @GET("/articles/{id}")
    void article(@Path("id") int articleId, Callback<JsonObject<Article>> callback);

    @GET("/authors")
    void authors(Callback<JsonArray<Author>> callback);

    @GET("/authors/{id}")
    void author(@Path("id") int authorId, Callback<JsonObject<Author>> callback);

}
