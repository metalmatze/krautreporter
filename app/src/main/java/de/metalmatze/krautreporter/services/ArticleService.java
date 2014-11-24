package de.metalmatze.krautreporter.services;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.metalmatze.krautreporter.KrautreporterRssParser;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleService {

    private static final String RSS_FILE = "krautreporter.rss";

    protected Context context;

    public ArticleService(Context applicationContext) {

        this.context = applicationContext;
    }

    public List<ArticleModel> all()
    {
        return new Select().from(ArticleModel.class).execute();
    }

    public void update()
    {
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(this.context.getAssets().open(RSS_FILE));
            List<ArticleModel> articleModelList = new KrautreporterRssParser.KrautreporterRssParserTask().execute(inputStream).get();

            ActiveAndroid.beginTransaction();
            try {
                for (ArticleModel articleModel : articleModelList)
                {
                    articleModel.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
