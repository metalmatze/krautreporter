package de.metalmatze.krautreporter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import de.metalmatze.krautreporter.adapters.RecyclerAdapter;
import de.metalmatze.krautreporter.entities.Article;

public class MainActivity extends ActionBarActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Article> articles = new ArrayList<Article>();
        RecyclerAdapter adapter = new RecyclerAdapter(articles);

        for (int i = 1; i <= 150; i++) {
            articles.add(new Article(i + ".10.2014", "Artikel Nummer " + i));
        }

        recyclerView.setAdapter(adapter);
    }

}
