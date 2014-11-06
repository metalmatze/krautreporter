package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.metalmatze.krautreporter.KrautreporterRssParser;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleRecyclerViewAdapter;
import de.metalmatze.krautreporter.entities.Article;

public class MainActivity extends ActionBarActivity {

    protected RecyclerView recyclerView;
    protected ArticleRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        this.recyclerView.setLayoutManager(layoutManager);

        BufferedInputStream inputStream = null;
        try {

            inputStream = new BufferedInputStream(getAssets().open("krautreporter.rss"));
            this.articles = new KrautreporterRssParser().parse(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        this.recyclerViewAdapter = new ArticleRecyclerViewAdapter(this.articles);
        this.recyclerView.setAdapter(this.recyclerViewAdapter);
    }

}
