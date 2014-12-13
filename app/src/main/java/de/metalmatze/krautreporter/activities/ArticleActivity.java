package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
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

public class ArticleActivity extends ActionBarActivity implements Html.ImageGetter {

    protected ArticleService articleService;
    private ArticleModel articleModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.articleService = new ArticleService(getApplicationContext());

        this.setContentView(R.layout.activity_article);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra("id", -1);

        this.articleModel = this.articleService.find(id);

        TextView articleTitle = (TextView) findViewById(R.id.article_title);
        TextView articleDate = (TextView) findViewById(R.id.article_date);
        final ImageView articleImage = (ImageView) findViewById(R.id.article_image);
        TextView articleExcerpt = (TextView) findViewById(R.id.article_excerpt);
        TextView articleContent = (TextView) findViewById(R.id.article_content);

        getSupportActionBar().setTitle(articleModel.title);

        Spanned contentFromHtml = Html.fromHtml(articleModel.content, this, null);
        SpannableStringBuilder contentStringBuilder = new SpannableStringBuilder(contentFromHtml);


        URLSpan[] urlSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), URLSpan.class);
        for (final URLSpan urlSpan : urlSpans)
        {
            int start = contentStringBuilder.getSpanStart(urlSpan);
            int end = contentStringBuilder.getSpanEnd(urlSpan);

            contentStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urlSpan.getURL()));

                    startActivity(intent);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            contentStringBuilder.removeSpan(urlSpan);
        }

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

        articleTitle.setText(articleModel.title);
        articleDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(articleModel.date.getTime()));
        articleExcerpt.setText(articleModel.excerpt);

        articleContent.setText(contentStringBuilder);
        articleContent.setLinkTextColor(getResources().getColor(R.color.krautAccent));
        articleContent.setMovementMethod(LinkMovementMethod.getInstance());

        Typeface typefaceTisaSans = Typeface.createFromAsset(getAssets(), "fonts/TisaSans.otf");
        articleContent.setTypeface(typefaceTisaSans);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.menu_article, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_browser)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse(this.articleModel.link));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Drawable getDrawable(String source) {
        return null;
    }
}
