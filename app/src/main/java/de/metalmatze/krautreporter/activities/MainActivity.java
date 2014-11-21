package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.activeandroid.query.Select;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

import de.metalmatze.krautreporter.KrautreporterRssParser;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.adapters.ArticleRecyclerViewAdapter;
import de.metalmatze.krautreporter.models.ArticleModel;

public class MainActivity extends ActionBarActivity {

    protected RecyclerView recyclerView;
    protected ArticleRecyclerViewAdapter recyclerViewAdapter;

    private List<ArticleModel> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NewRelic.withApplicationToken("AAcd87d4b9197cb6e3184ac6d5b78f1f1d42488de6").start(this.getApplication());

        this.setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        this.recyclerView.setLayoutManager(layoutManager);

        int count = new Select().from(ArticleModel.class).count();

        Log.d("article count", String.valueOf(count));

        if (count < 1)
        {
            try {

                BufferedInputStream inputStream = new BufferedInputStream(getAssets().open("krautreporter.rss"));
                this.articles = new KrautreporterRssParser().parse(inputStream);

                for (ArticleModel articleModel: this.articles)
                {
                    articleModel.save();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } else {
            this.articles = new Select().from(ArticleModel.class).execute();
        }

        this.recyclerViewAdapter = new ArticleRecyclerViewAdapter(this.articles);
        this.recyclerView.setAdapter(this.recyclerViewAdapter);
    }

}
