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
import de.metalmatze.krautreporter.models.ArticleModel;
import de.metalmatze.krautreporter.services.ArticleService;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity implements ArticlesAdapter.OnItemClickListener, Response.ErrorListener, Response.Listener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected RecyclerView recyclerView;
    protected ArticleService articleService;

    private List<ArticleModel> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        this.articleService = new ArticleService(getApplicationContext());

        this.setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(layoutManager);

        this.articleService.update(this, this);

        this.articles = this.articleService.all();
        this.setArticles(this.articles);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<ArticleModel> articles = this.articleService.all();

        this.setArticles(articles);
    }

    public void setArticles(List<ArticleModel> articles) {
        this.articles = articles;
        ArticlesAdapter articlesAdapter = new ArticlesAdapter(getApplicationContext(), this, this.articles);
        this.recyclerView.setAdapter(articlesAdapter);
    }

    @Override
    public void onItemClick(ArticleModel articleModel) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("id", articleModel.getId());

        this.startActivity(intent);
    }

    @Override
    public void onResponse(Object response) {
        List<ArticleModel> responseArticles = (List<ArticleModel>) response;

        List<ArticleModel> articles = this.articleService.saveModels(responseArticles);

        this.setArticles(articles);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

}
