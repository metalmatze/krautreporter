package de.metalmatze.krautreporter.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class Api {

    public static final String URL = "http://krautreporter.metalmatze.de";

    protected Realm realm;

    private static ApiInterface singleton;

    public static ApiInterface request() {

        if (singleton == null) {

            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(URL)
                    .setConverter(new GsonConverter(gson))
                    .build();

            singleton = restAdapter.create(ApiInterface.class);
        }

        return singleton;
    }

    public static Api with(Realm realm) {
        return new Api(realm);
    }

    public Api(Realm realm) {
        this.realm = realm;
    }

    public void updateArticles() {
        Api.request().articles(new Callback<JsonArray<Article>>() {
            @Override
            public void success(JsonArray<Article> articles, Response response) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(articles.data);
                realm.commitTransaction();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void updateAuthors() {
        Api.request().authors(new Callback<JsonArray<Author>>() {
            @Override
            public void success(JsonArray<Author> authors, Response response) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(authors.data);
                realm.commitTransaction();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
