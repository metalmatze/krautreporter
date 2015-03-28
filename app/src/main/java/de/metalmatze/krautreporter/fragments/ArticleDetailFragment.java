package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Author;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleDetailFragment extends Fragment {

    public interface ActionBarTitle {
        public void setActionBarTitle(String title);
    }

    public static final String ARTICLE_ID = "article_id";

    private Article article;
    private Author author;
    private ActionBarTitle actionBarTitle;

    @InjectView(R.id.author_name) TextView articleAuthorName;
    @InjectView(R.id.article_headline) TextView articleHeadline;
    @InjectView(R.id.article_date) TextView articleDate;
    @InjectView(R.id.article_excerpt) TextView articleExcerpt;
    @InjectView(R.id.article_content) TextView articleContent;

    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getInstance(getActivity().getApplicationContext());

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

        author = realm.where(Author.class).equalTo("id", article.getAuthor()).findFirst();
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

            setArticleAuthorName(author.getName());
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

    private void setDate(String date) {
        articleDate.setText(date);
    }

    private void setExcerpt(String excerpt) {
        articleExcerpt.setText(excerpt);
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

        QuoteSpan[] quoteSpans = contentStringBuilder.getSpans(0, contentStringBuilder.length(), QuoteSpan.class);
        for (QuoteSpan oldQuoteSpan : quoteSpans) {
            int start = contentStringBuilder.getSpanStart(oldQuoteSpan);
            int end = contentStringBuilder.getSpanEnd(oldQuoteSpan);

            QuoteSpan quoteSpan = new QuoteSpan(0xff000000);

            contentStringBuilder.removeSpan(oldQuoteSpan);
            contentStringBuilder.setSpan(quoteSpan, start, end, 0);
        }

        articleContent.setText(contentStringBuilder);
        articleContent.setLinkTextColor(getResources().getColor(R.color.krautAccent));
        articleContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setArticleAuthorName(String name) {
        articleAuthorName.setText(name);
    }

}
