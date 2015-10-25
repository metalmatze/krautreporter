package de.metalmatze.krautreporter.api;

import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ApiInterface {
    @GET("/articles")
    Observable<JsonArray<Article>> articles();

    @GET("/articles")
    Observable<JsonArray<Article>> articlesOlderThan(@Query("olderthan") int id);

    @GET("/authors")
    Observable<JsonArray<Author>> authors();
}
