package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticlesAdapter;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.services.ArticleServiceActiveAndroid;
import de.metalmatze.krautreporter.services.ArticleService;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity implements ArticlesAdapter.OnItemClickListener, Response.ErrorListener, Response.Listener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected RecyclerView recyclerView;
    protected ArticlesAdapter articlesAdapter;
    protected ArticleService articleService;

    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        articleService = new ArticleServiceActiveAndroid(this);
        articles = articleService.all();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        articlesAdapter = new ArticlesAdapter(getApplicationContext(), this, articles);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(articlesAdapter);

        articleService.update(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshArticles(articleService.all());
    }

    public void refreshArticles(List<Article> articles) {

        articles.removeAll(this.articles);
        this.articles.addAll(articles);

        articlesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("id", article.getId());

        this.startActivity(intent);
    }

    @Override
    public void onResponse(Object response) {
        List<Article> articles = articleService.save((List<Article>) response);

        refreshArticles(articles);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, getString(R.string.error_response_rss), Toast.LENGTH_SHORT).show();
    }

}
