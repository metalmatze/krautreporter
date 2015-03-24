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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.helpers.Checksum;
import de.metalmatze.krautreporter.models.Article;
import io.realm.Realm;

public class ArticleActivity extends ActionBarActivity {

    private static final String LOG_TAG = ArticleActivity.class.getSimpleName();
    protected Picasso picasso;

    private Article article;
    private Typeface typefaceTisaSans;
    private Typeface typefaceTisaSansBold;
    private List<Target> picassoTargets = new LinkedList<Target>();

    @InjectView(R.id.article_title) TextView articleHeadline;
    @InjectView(R.id.article_date) TextView articleDate;
    @InjectView(R.id.article_image) ImageView articleImage;
    @InjectView(R.id.article_excerpt) TextView articleExcerpt;
    @InjectView(R.id.article_content) TextView articleContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        ButterKnife.inject(this);

        picasso = Picasso.with(this);

        this.typefaceTisaSans = Typeface.createFromAsset(getAssets(), "fonts/TisaSans.otf");
        this.typefaceTisaSansBold = Typeface.createFromAsset(getAssets(), "fonts/TisaSans-Bold.otf");

        int id = getIntent().getIntExtra("id", -1);

        Realm realm = Realm.getInstance(getApplicationContext());

        this.article = realm.where(Article.class).equalTo("id", id).findFirst();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(article.getTitle());
        }

        setHeadline(article.getHeadline());
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

            intent.setData(Uri.parse(getString(R.string.url_krautreporter) + this.article.getUrl()));
            startActivity(intent);
        }

        if (id == R.id.action_share)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Krautreporter: " + this.article.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_krautreporter) + article.getUrl());

            startActivity(Intent.createChooser(intent, getString(R.string.share_article)));
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.article_image)
    public void onImageClick(ImageView image) {
        Drawable drawable = image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        try {
            File file = saveBitmapToExternalFilesDir(article.getImage(), bitmap);
            startImageIntent(file);

        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_open_image), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
            e.printStackTrace();
        }

    }

    private void startImageIntent(File file) {
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");

        startActivity(intent);
    }

    private File saveBitmapToExternalFilesDir(String url, Bitmap bitmap) throws IOException {
        try {
            String fileName = Checksum.md5(url);

            File file = new File(getExternalFilesDir(null), String.format("%s.jpg", fileName));

            if (file.exists()) {
                return file;
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return file;
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Couldn't hash url with md5");
        }
    }

    private void setExcerpt(String excerpt) {
        this.articleExcerpt.setText(excerpt);
        this.articleExcerpt.setTypeface(typefaceTisaSansBold);
    }

    private void setHeadline(String headline) {
        this.articleHeadline.setText(headline);
        this.articleHeadline.setTypeface(typefaceTisaSansBold);
    }

    private void setDate(String date) {
//        String dateFormated = new SimpleDateFormat("dd.MM.yyyy").format(date.getTime());
        this.articleDate.setText(date);
        this.articleDate.setTypeface(typefaceTisaSans);
    }

    private void setImage(String image) {
        if (image != null) {
            picasso.load(getString(R.string.url_krautreporter) + image).into(articleImage);
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
                    intent.setData(Uri.parse(urlSpan.getURL().trim()));

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
            final String imageUrl = getResources().getString(R.string.url_krautreporter) + imageSpan.getSource().replace("/w300_", "/w1000_");

            Target picassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    final ImageSpan newImageSpan = new ImageSpan(getApplicationContext(), bitmap);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            try {
                                File imageFile = saveBitmapToExternalFilesDir(imageUrl, bitmap);
                                startImageIntent(imageFile);
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_open_image), Toast.LENGTH_SHORT).show();
                                Crashlytics.logException(e);
                                e.printStackTrace();
                            }
                        }
                    };

                    contentStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
