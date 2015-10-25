package de.metalmatze.krautreporter.api;

import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import retrofit.http.GET;
import rx.Observable;

public interface ApiInterface {
    @GET("/articles")
    Observable<JsonArray<Article>> articles();

    @GET("/authors")
    Observable<JsonArray<Author>> authors();
}
