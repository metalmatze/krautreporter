package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.ArticleModel;
import de.metalmatze.krautreporter.services.ArticleService;

public class ArticleActivity extends ActionBarActivity implements Html.ImageGetter {

    protected ArticleService articleService;
    protected RequestQueue requestsQueue;

    private ArticleModel articleModel;
    private TextView articleTitle;
    private TextView articleDate;
    private ImageView articleImage;
    private TextView articleExcerpt;
    private TextView articleContent;
    private Typeface typefaceTisaSans;
    private Typeface typefaceTisaSansBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.articleService = new ArticleService(getApplicationContext());
        this.requestsQueue = Volley.newRequestQueue(this);

        this.typefaceTisaSans = Typeface.createFromAsset(getAssets(), "fonts/TisaSans.otf");
        this.typefaceTisaSansBold = Typeface.createFromAsset(getAssets(), "fonts/TisaSans-Bold.otf");

        setContentView(R.layout.activity_article);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        long id = getIntent().getLongExtra("id", -1);

        this.articleModel = this.articleService.find(id);

        this.articleTitle = (TextView) findViewById(R.id.article_title);
        this.articleDate = (TextView) findViewById(R.id.article_date);
        this.articleImage = (ImageView) findViewById(R.id.article_image);
        this.articleExcerpt = (TextView) findViewById(R.id.article_excerpt);
        this.articleContent = (TextView) findViewById(R.id.article_content);

        setTitle(articleModel.title);
        setDate(articleModel.date);
        setImage(articleModel.image);
        setExcerpt(articleModel.excerpt);
        setContent(articleModel.content);
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

        if (id == R.id.action_share)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Krautreporter: " + this.articleModel.title);
            intent.putExtra(Intent.EXTRA_TEXT, this.articleModel.link);

            startActivity(Intent.createChooser(intent, getString(R.string.share_article)));
        }

        return super.onOptionsItemSelected(item);
    }

    private void setExcerpt(String excerpt) {
        this.articleExcerpt.setText(excerpt);
        this.articleExcerpt.setTypeface(typefaceTisaSansBold);
    }

    private void setTitle(String title) {
        getSupportActionBar().setTitle(title);
        this.articleTitle.setText(title);
        this.articleTitle.setTypeface(typefaceTisaSansBold);
    }

    private void setDate(Date date) {
        String dateFormated = new SimpleDateFormat("dd.MM.yyyy").format(date.getTime());
        this.articleDate.setText(dateFormated);
        this.articleDate.setTypeface(typefaceTisaSans);
    }

    private void setImage(String image) {
        if (image != null)
        {
            ImageRequest request = new ImageRequest(image,
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

            this.requestsQueue.add(request);
        }
    }

    private void setContent(String content) {
        Spanned contentFromHtml = Html.fromHtml(content, this, null);
        final SpannableStringBuilder contentStringBuilder = new SpannableStringBuilder(contentFromHtml);

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

        ImageSpan[] imageSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), ImageSpan.class);
        for (final ImageSpan imageSpan : imageSpans)
        {
            final int start = contentStringBuilder.getSpanStart(imageSpan);
            final int end = contentStringBuilder.getSpanEnd(imageSpan);

            final String imageUrl = getResources().getString(R.string.url_base) + imageSpan.getSource();

            ImageRequest imageRequest = new ImageRequest(imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {

                            ImageSpan newImageSpan = new ImageSpan(getApplicationContext(), response);
                            contentStringBuilder.setSpan(newImageSpan, start, end, ImageSpan.ALIGN_BASELINE);
                            contentStringBuilder.removeSpan(imageSpan);
                            articleContent.setText(contentStringBuilder);

                        }
                    },
                    0, 0, null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            );

            this.requestsQueue.add(imageRequest);
        }

        articleContent.setText(contentStringBuilder);
        articleContent.setTypeface(typefaceTisaSans);
        articleContent.setLinkTextColor(getResources().getColor(R.color.krautAccent));
        articleContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public Drawable getDrawable(String source) {
        return null;
    }
}
