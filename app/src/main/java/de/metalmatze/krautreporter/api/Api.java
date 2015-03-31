package de.metalmatze.krautreporter.api;

import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import de.metalmatze.krautreporter.models.Image;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class Api {

    public interface ApiCallback {
        public void finished();
    }

    public static final String URL = "http://krautreporter.metalmatze.de";
    public static final String LOG_TAG = Api.class.getSimpleName();

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
                    .excludeFieldsWithoutExposeAnnotation()
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

    public void updateArticles(@Nullable final ApiCallback apiCallback) {
        Api.request().articles(new Callback<JsonArray<Article>>() {
            @Override
            public void success(JsonArray<Article> jsonArticles, Response response) {
                realm.beginTransaction();
                List<Article> realmArticles = realm.copyToRealmOrUpdate(jsonArticles.data);

                int articleIndex = 0;
                for(Article article : realmArticles) {
                    int authorId = jsonArticles.data.get(articleIndex).getSerializedAuthor();
                    Author author = realm.where(Author.class)
                                            .equalTo("id", authorId)
                                            .findFirst();
                    article.setAuthor(author);

                    List<Image> serializedImages = jsonArticles.data.get(articleIndex).getSerializedImages().data;
                    List<Image> realmImages = realm.copyToRealmOrUpdate(serializedImages);

                    for (Image image : realmImages) {
                        article.getImages().add(image);
                    }

                    articleIndex++;
                }

                realm.commitTransaction();

                if (apiCallback != null) {
                    apiCallback.finished();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();

                if (apiCallback != null) {
                    apiCallback.finished();
                }
            }
        });
    }

    public void updateAuthors(@Nullable final ApiCallback apiCallback) {
        Api.request().authors(new Callback<JsonArray<Author>>() {
            @Override
            public void success(JsonArray<Author> jsonAuthors, Response response) {
                realm.beginTransaction();
                List<Author> realmAuthors = realm.copyToRealmOrUpdate(jsonAuthors.data);

                int authorIndex = 0;
                for (Author author : realmAuthors) {
                    List<Image> serializedImages = jsonAuthors.data.get(authorIndex).getSerializedImages().data;
                    List<Image> realmImages = realm.copyToRealmOrUpdate(serializedImages);

                    for (Image image : realmImages) {
                        author.getImages().add(image);
                    }

                    authorIndex++;
                }

                realm.commitTransaction();

                if(apiCallback != null) {
                    apiCallback.finished();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if(apiCallback != null) {
                    apiCallback.finished();
                }
            }
        });
    }
}
