package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.helpers.ClickableImageSpan;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.services.ArticleService;
import de.metalmatze.krautreporter.services.ArticleServiceActiveAndroid;

public class ArticleActivity extends ActionBarActivity {

    private static final String LOG_TAG = ArticleActivity.class.getSimpleName();
    protected ArticleService articleService;
    protected Picasso picasso;

    private Article article;
    private Typeface typefaceTisaSans;
    private Typeface typefaceTisaSansBold;
    private List<Target> picassoTargets = new LinkedList<Target>();

    @InjectView(R.id.article_title) TextView articleTitle;
    @InjectView(R.id.article_date) TextView articleDate;
    @InjectView(R.id.article_image) ImageView articleImage;
    @InjectView(R.id.article_excerpt) TextView articleExcerpt;
    @InjectView(R.id.article_content) TextView articleContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        ButterKnife.inject(this);

        this.articleService = new ArticleServiceActiveAndroid(this);
        picasso = Picasso.with(this);

        this.typefaceTisaSans = Typeface.createFromAsset(getAssets(), "fonts/TisaSans.otf");
        this.typefaceTisaSansBold = Typeface.createFromAsset(getAssets(), "fonts/TisaSans-Bold.otf");


        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        long id = getIntent().getLongExtra("id", -1);

        this.article = this.articleService.find(id);

        setTitle(article.getTitle());
        setDate(article.getDate());
        setImage(article.getImage());
        setExcerpt(article.getExcerpt());
        setContent(article.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_article, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_browser)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse(this.article.getLink()));
            startActivity(intent);
        }

        if (id == R.id.action_share)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Krautreporter: " + this.article.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, this.article.getLink());

            startActivity(Intent.createChooser(intent, getString(R.string.share_article)));
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.article_image)
    public void onImageClick(ImageView image) {
        Drawable drawable = image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(article.getImage().getBytes());
            byte[] digest = messageDigest.digest();

            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(String.format("%02x", b & 0xff));
            }

            String md5 = stringBuffer.toString();

            Log.d(LOG_TAG, md5);

            File file = new File(getExternalFilesDir(null), md5);

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(file);

            Log.d(LOG_TAG, uri.toString());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");

            startActivity(intent);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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
        if (image != null) {
            picasso.load(image).into(articleImage);
            articleImage.setVisibility(View.VISIBLE);
        }
    }

    private void setContent(final String content) {
        Spanned contentFromHtml = Html.fromHtml(content);
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

            final String imageUrl = getResources().getString(R.string.url_base) + imageSpan.getSource().replace("/w300_", "/w1000_");

            Target picassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ImageSpan newImageSpan = new ClickableImageSpan(getApplicationContext(), bitmap);

                    contentStringBuilder.setSpan(newImageSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    contentStringBuilder.removeSpan(imageSpan);
                    articleContent.setText(contentStringBuilder);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(LOG_TAG, String.format("Picasso fails to load: %s", imageUrl));
                    contentStringBuilder.removeSpan(imageSpan);
                    articleContent.setText(contentStringBuilder);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            picassoTargets.add(picassoTarget);

            picasso.load(imageUrl).into(picassoTarget);
        }

        QuoteSpan[] quoteSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), QuoteSpan.class);
        for (QuoteSpan oldQuoteSpan : quoteSpans) {
            int start = contentStringBuilder.getSpanStart(oldQuoteSpan);
            int end = contentStringBuilder.getSpanEnd(oldQuoteSpan);

            QuoteSpan quoteSpan = new QuoteSpan(0xff000000);

            contentStringBuilder.removeSpan(oldQuoteSpan);
            contentStringBuilder.setSpan(quoteSpan, start, end, 0);
        }

        articleContent.setText(contentStringBuilder);
        articleContent.setTypeface(typefaceTisaSans);
        articleContent.setLinkTextColor(getResources().getColor(R.color.krautAccent));
        articleContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
