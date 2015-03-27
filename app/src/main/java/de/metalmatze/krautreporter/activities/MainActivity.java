package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleAdapter;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends ActionBarActivity implements ArticleAdapter.OnItemClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final Realm realm = Realm.getInstance(this);

        RealmResults<Article> articles = realm.where(Article.class).findAll();
        articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final ArticleAdapter articleAdapter = new ArticleAdapter(getApplicationContext(), articles, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(articleAdapter);

        Api.with(realm).updateAuthors();
        Api.with(realm).updateArticles();

        Log.d(LOG_TAG + " articles: ", String.valueOf(realm.where(Article.class).count()));
        Log.d(LOG_TAG + " authors: ", String.valueOf(realm.where(Author.class).count()));

        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                articleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("id", article.getId());

        startActivity(intent);
    }
}
