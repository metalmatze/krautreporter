package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Image;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleDetailFragment extends Fragment {

    public interface ActionBarTitle {
        public void setActionBarTitle(String title);
    }

    public static final String ARTICLE_ID = "article_id";
    public static final String LOG_TAG = ArticleDetailFragment.class.getSimpleName();

    private Context context;

    private ActionBarTitle actionBarTitle;
    private Picasso picasso;
    private List<Target> picassoTargets = new ArrayList<>();

    private Article article;

    @InjectView(R.id.author_image) ImageView articleAuthorImage;
    @InjectView(R.id.author_name) TextView articleAuthorName;
    @InjectView(R.id.article_headline) TextView articleHeadline;
    @InjectView(R.id.article_date) TextView articleDate;
    @InjectView(R.id.article_image) ImageView articleImage;
    @InjectView(R.id.article_excerpt) TextView articleExcerpt;
    @InjectView(R.id.article_content) TextView articleContent;

    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();

        Realm realm = Realm.getInstance(context);
        picasso = Picasso.with(context);

        if (getArguments().containsKey(ARTICLE_ID) && getArguments().getInt(ARTICLE_ID) >= 0) {
            article = realm
                        .where(Article.class)
                        .equalTo("id", getArguments().getInt(ARTICLE_ID))
                        .findFirst();
        }
        else {
            RealmResults<Article> articles = realm.where(Article.class).findAll();
            articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

            if(articles.size() > 0) {
                article = articles.first();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        actionBarTitle = (ActionBarTitle) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);

        if (article != null) {
            Typeface typefaceTisaSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TisaSans-Bold.otf");

            articleAuthorName.setTypeface(typefaceTisaSans);
            articleHeadline.setTypeface(typefaceTisaSansBold);
            articleDate.setTypeface(typefaceTisaSans);
            articleExcerpt.setTypeface(typefaceTisaSansBold);
            articleContent.setTypeface(typefaceTisaSans);

            actionBarTitle.setActionBarTitle(article.getTitle());

            setHeadline(article.getHeadline());
            setDate(article.getDate());
            setExcerpt(article.getExcerpt());
            setContent(article.getContent());
            setArticleAuthorName(article.getAuthor().getName());

            Image articleImage = article.getImages().where().equalTo("width", 1000).findFirst();
            if (articleImage != null) {
                setImage(articleImage.getSrc());
            }

            Image authorImage = article.getAuthor().getImages().where().equalTo("width", 340).findFirst();
            if (authorImage != null) {
                setArticleAuthorImage(authorImage.getSrc());
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_article, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.url_krautreporter) + article.getUrl()));

            startActivity(intent);
        }

        if (itemId == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Krautreporter: " + this.article.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_krautreporter) + article.getUrl());

            startActivity(Intent.createChooser(intent, getString(R.string.share_article)));
        }

        return super.onOptionsItemSelected(item);
    }

    private void setHeadline(String headline) {
        articleHeadline.setText(headline);
    }

    private void setDate(Date date) {
        DateFormat dateInstance = DateFormat.getDateInstance();
        String dateText = dateInstance.format(date.getTime());
        articleDate.setText(dateText);
    }

    private void setImage(String url) {
        articleImage.setVisibility(View.VISIBLE);
        picasso.load(getString(R.string.url_krautreporter) + url).into(articleImage);
    }

    private void setExcerpt(String excerpt) {
        articleExcerpt.setText(excerpt);
    }

    private void setContent(final String content) {
        Spanned contentFromHtml = Html.fromHtml(content);
        final SpannableStringBuilder contentStringBuilder = new SpannableStringBuilder(contentFromHtml);

        parseContentUrls(contentStringBuilder);
        parseContentQuotes(contentStringBuilder);
        parseContentImages(contentStringBuilder);

        articleContent.setText(contentStringBuilder);
        articleContent.setLinkTextColor(getResources().getColor(R.color.krautAccent));
        articleContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void parseContentQuotes(SpannableStringBuilder contentStringBuilder) {
        QuoteSpan[] quoteSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), QuoteSpan.class);
        for (QuoteSpan oldQuoteSpan : quoteSpans) {
            int start = contentStringBuilder.getSpanStart(oldQuoteSpan);
            int end = contentStringBuilder.getSpanEnd(oldQuoteSpan);

            QuoteSpan quoteSpan = new QuoteSpan(0xff000000);

            contentStringBuilder.removeSpan(oldQuoteSpan);
            contentStringBuilder.setSpan(quoteSpan, start, end, 0);
        }
    }

    private void parseContentUrls(SpannableStringBuilder contentStringBuilder) {
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
    }

    private void parseContentImages(final SpannableStringBuilder contentStringBuilder) {
        ImageSpan[] imageSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), ImageSpan.class);
        for (final ImageSpan imageSpan : imageSpans)
        {
            final int start = contentStringBuilder.getSpanStart(imageSpan);
            final int end = contentStringBuilder.getSpanEnd(imageSpan);
            final String imageUrl = getResources().getString(R.string.url_krautreporter) + imageSpan.getSource().replace("/w300_", "/w1000_");

            Target picassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    final ImageSpan newImageSpan = new ImageSpan(context, bitmap);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
//                            try {
//                                File imageFile = saveBitmapToExternalFilesDir(imageUrl, bitmap);
//                                startImageIntent(imageFile);
//                            } catch (IOException e) {
//                                Toast.makeText(context, getString(R.string.error_open_image), Toast.LENGTH_SHORT).show();
//                                Crashlytics.logException(e);
//                                e.printStackTrace();
//                            }
                        }
                    };

                    contentStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    contentStringBuilder.setSpan(newImageSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    contentStringBuilder.removeSpan(imageSpan);
                    articleContent.setText(contentStringBuilder);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(getClass().getSimpleName(), String.format("Picasso fails to load: %s", imageUrl));
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
    }

    private void setArticleAuthorName(String name) {
        articleAuthorName.setText(name);
    }

    private void setArticleAuthorImage(String url) {
        picasso.load(getString(R.string.url_krautreporter) + url).into(articleAuthorImage);
    }

}
