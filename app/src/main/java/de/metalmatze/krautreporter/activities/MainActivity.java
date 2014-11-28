package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleRecyclerViewAdapter;
import de.metalmatze.krautreporter.models.ArticleModel;
import de.metalmatze.krautreporter.services.ArticleService;

public class MainActivity extends ActionBarActivity {

    protected RecyclerView recyclerView;
    protected ArticleRecyclerViewAdapter recyclerViewAdapter;
    protected ArticleService articleService;

    private List<ArticleModel> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.articleService = new ArticleService(getApplicationContext());
//        NewRelic.withApplicationToken("AAcd87d4b9197cb6e3184ac6d5b78f1f1d42488de6").start(this.getApplication());

        this.setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        this.recyclerView.setLayoutManager(layoutManager);

        this.articleService.update();
        this.articles = this.articleService.all();

        this.recyclerViewAdapter = new ArticleRecyclerViewAdapter(getApplicationContext(), this.articles);
        this.recyclerView.setAdapter(this.recyclerViewAdapter);
    }

}
