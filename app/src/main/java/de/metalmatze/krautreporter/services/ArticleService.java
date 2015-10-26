package de.metalmatze.krautreporter.services;

import android.content.Context;

import java.util.List;

import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import de.metalmatze.krautreporter.models.Image;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticleService {

    public static final String SORT_FIELD = "order";

    private Api api;
    private Realm realm;

    public ArticleService(Context context, Api api) {
        this.api = api;
        this.realm = Realm.getInstance(context);
    }

    public Observable<List<Article>> getArticles() {
        RealmResults<Article> articles = realm.where(Article.class).findAll();
        articles.sort(SORT_FIELD, RealmResults.SORT_ORDER_DESCENDING);
        return Observable.just(articles);
    }

    public Observable<List<Article>> updateArticles() {
        return api.request().articles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(articles -> articles.data)
                .map(this::copyOrUpdateArticles);
    }

    public Observable<List<Article>> getArticlesOlderThan(int id) {
        return api.request().articlesOlderThan(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(articles -> articles.data)
                .map(this::copyOrUpdateArticles);
    }

    private List<Article> copyOrUpdateArticles(List<Article> articles) {
        realm.beginTransaction();
        List<Article> realmArticles = realm.copyToRealmOrUpdate(articles);

        int index = 0;
        for (Article article : realmArticles) {
            int authorId = articles.get(index).getSerializedAuthor();
            Author author = realm.where(Author.class)
                    .equalTo("id", authorId)
                    .findFirst();
            article.setAuthor(author);

            List<Image> serializedImages = articles.get(index).getSerializedImages().data;
            List<Image> realmImages = realm.copyToRealmOrUpdate(serializedImages);

            for (Image image : realmImages) {
                article.getImages().add(image);
            }

            index++;
        }

        realm.commitTransaction();

        RealmResults realmResults = realm.where(Article.class).findAll();
        realmResults.sort(SORT_FIELD, RealmResults.SORT_ORDER_DESCENDING);

        return realmResults;
    }
}
