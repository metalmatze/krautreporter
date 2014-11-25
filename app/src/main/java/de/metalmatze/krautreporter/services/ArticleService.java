package de.metalmatze.krautreporter.services;

import android.content.Context;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.metalmatze.krautreporter.KrautreporterRssParser;
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

    public void update()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        StringRequest rssRequest = new StringRequest(
            Request.Method.GET,
            RSS_HOST + RSS_FILE,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {

                        List<ArticleModel> articleModels = new KrautreporterRssParser.KrautreporterRssParserTask().execute(s).get();
                        saveModels(articleModels);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    Toast.makeText(context, "Error loading feed", Toast.LENGTH_SHORT).show();
                }
            }
        );

        requestQueue.add(rssRequest);
    }

    private void saveModels(List<ArticleModel> models)
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
    }

}
