package de.metalmatze.krautreporter.services;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.List;

import de.metalmatze.krautreporter.helpers.RssRequest;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleService {

    private static final String RSS_HOST = "https://krautreporter.de/";
    private static final String RSS_FILE = "rss.rss";

    protected Context context;

    public ArticleService(Context applicationContext) {

        this.context = applicationContext;
    }

    public List<ArticleModel> all()
    {
        return new Select().from(ArticleModel.class).orderBy("date DESC").execute();
    }

    public ArticleModel find(long id)
    {
        return new Select().from(ArticleModel.class).where("id = ?", id).executeSingle();
    }

    public void update(Response.Listener listener, Response.ErrorListener errorListener)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(new RssRequest(Request.Method.GET, RSS_HOST + RSS_FILE, listener, errorListener));
    }

    public List<ArticleModel> saveModels(List<ArticleModel> models)
    {
        ActiveAndroid.beginTransaction();
        try {
            for (ArticleModel article : models)
            {
                article.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }

        return this.all();
    }

}
