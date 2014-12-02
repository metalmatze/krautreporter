package de.metalmatze.krautreporter.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.ArticleModel;
import de.metalmatze.krautreporter.services.ArticleService;

public class ArticleActivity extends ActionBarActivity {

    protected ArticleService articleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.articleService = new ArticleService(getApplicationContext());

        this.setContentView(R.layout.activity_article);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra("id", -1);
        ArticleModel articleModel = this.articleService.find(id);

        TextView articleTitle = (TextView) findViewById(R.id.article_title);
        TextView articleDate = (TextView) findViewById(R.id.article_date);
        final ImageView articleImage = (ImageView) findViewById(R.id.article_image);
        TextView articleExcerpt = (TextView) findViewById(R.id.article_excerpt);
        TextView articleContent = (TextView) findViewById(R.id.article_content);

        getSupportActionBar().setTitle(articleModel.title);

        Spanned contentFromHtml = Html.fromHtml(articleModel.content);

        articleTitle.setText(articleModel.title);
        articleDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(articleModel.date.getTime()));
        articleContent.setText(contentFromHtml);

        Linkify.addLinks(articleContent, Linkify.WEB_URLS);

        if (articleModel.image != null)
        {
            ImageRequest request = new ImageRequest(articleModel.image,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                    articleImage.setImageBitmap(bitmap);
                    articleImage.setVisibility(View.VISIBLE);
                    }
                }
                , 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
            );

            Volley.newRequestQueue(this).add(request);
        }
    }

}
