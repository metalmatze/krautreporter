package de.metalmatze.krautreporter.services;

import android.content.Context;

import java.util.List;

import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.models.Author;
import de.metalmatze.krautreporter.models.Image;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthorService {
    private final Api api;
    private Realm realm;

    public AuthorService(Context context, Api api) {
        this.api = api;
        this.realm = Realm.getInstance(context);
    }

    public Observable<List<Author>> getAuthors() {
        List<Author> authors = realm.where(Author.class).findAll();
        return Observable.just(authors);
    }

    public Observable<List<Author>> updateAuthors() {
        return api.request().authors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(authors -> authors.data)
                .map(authors -> {
                    realm.beginTransaction();
                    List<Author> realmAuthors = realm.copyToRealmOrUpdate(authors);

                    int index = 0;
                    for (Author author : realmAuthors) {
                        List<Image> images = authors.get(index).getSerializedImages().data;
                        List<Image> realmImages = realm.copyToRealmOrUpdate(images);

                        for (Image image : realmImages) {
                            author.getImages().add(image);
                        }

                        index++;
                    }

                    realm.commitTransaction();

                    return realmAuthors;
                });
    }
}
