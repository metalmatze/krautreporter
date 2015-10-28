package de.metalmatze.krautreporter.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import de.metalmatze.krautreporter.models.Image;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleDetailFragment extends Fragment {

    public interface ActionBarTitle {
        void setActionBarTitle(String title);
    }

    public static final String ARTICLE_ID = "article_id";

    private Context context;

    private ActionBarTitle actionBarTitle;
    private Picasso picasso;

    private Article article;
    private Date readingBegin;
    private long readingDuration = 0;

    @Bind(R.id.author)
    RelativeLayout articleAuthor;
    @Bind(R.id.author_image)
    ImageView articleAuthorImage;
    @Bind(R.id.author_name)
    TextView articleAuthorName;
    @Bind(R.id.article_headline)
    TextView articleHeadline;
    @Bind(R.id.article_date)
    TextView articleDate;
    @Bind(R.id.article_image)
    ImageView articleImage;
    @Bind(R.id.article_image_progressbar)
    ProgressBar articleImageProgressBar;
    @Bind(R.id.article_excerpt)
    TextView articleExcerpt;
    @Bind(R.id.article_content)
    WebView articleContent;

    public ArticleDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        actionBarTitle = (ActionBarTitle) context;
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
        } else {
            RealmResults<Article> articles = realm.where(Article.class).findAll();
            articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

            if (articles.size() > 0) {
                article = articles.first();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);

        if (article != null) {
            Typeface typefaceTisaSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TisaSans.otf");
            Typeface typefaceTisaSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TisaSans-Bold.otf");

            articleAuthorName.setTypeface(typefaceTisaSans);
            articleHeadline.setTypeface(typefaceTisaSansBold);
            articleDate.setTypeface(typefaceTisaSans);
            articleExcerpt.setTypeface(typefaceTisaSansBold);

            actionBarTitle.setActionBarTitle(article.getTitle());

            setHeadline(article.getHeadline());
            setDate(article.getDate());
            setExcerpt(article.getExcerpt());
            setContent(article.getContent());
            setArticleAuthorName(article.getAuthor().getName());

            Image articleImage = article.getImages().where().equalTo("width", 1000).findFirst();
            if (articleImage != null) {
                setImage(articleImage.getSrc());
            } else {
                articleImageProgressBar.setVisibility(View.GONE);
            }

            Image authorImage = article.getAuthor().getImages().where().equalTo("width", 340).findFirst();
            if (authorImage != null) {
                setArticleAuthorImage(authorImage.getSrc());
            }

            articleAuthor.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article.getAuthor().getUrl()));
                startActivity(intent);
            });
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        readingBegin = new Date();
    }

    @Override
    public void onPause() {
        super.onPause();

        Date now = new Date();
        long currentReadingDuration = (now.getTime() - readingBegin.getTime()) / 1000 % 60;
        readingDuration = readingDuration + currentReadingDuration;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_article, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(article.getUrl()));

            startActivity(intent);
        }

        if (itemId == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Krautreporter: " + this.article.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, article.getUrl());

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
        articleImageProgressBar.setVisibility(View.VISIBLE);

        picasso.load(url).into(articleImage, new Callback() {
            @Override
            public void onSuccess() {
                articleImage.setVisibility(View.VISIBLE);
                articleImageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                articleImageProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setExcerpt(String excerpt) {
        articleExcerpt.setText(excerpt);
    }

    private void setContent(String content) {
        WebSettings webSettings = articleContent.getSettings();

        content = String.format("<link rel='stylesheet' type='text/css' href='file:///android_asset/content.css' />%s", content);
        content = String.format("<base href='%s'>%s", getString(R.string.url_krautreporter), content);

        content = setContentImages(content);

        articleContent.loadDataWithBaseURL("file:///android_assest", content, "text/html", "utf-8", null);
        articleContent.setPadding(0, 0, 0, 0);
        articleContent.setBackgroundColor(getResources().getColor(R.color.background));
        articleContent.setVerticalScrollBarEnabled(false);
        articleContent.setHorizontalScrollBarEnabled(false);

        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultFontSize(18);
    }

    private String setContentImages(String content) {
        Document document = Jsoup.parse(content);

        Elements imgs = document.getElementsByTag("img");
        for (Element img : imgs) {
            String srcset = img.attr("srcset");

            Pattern pattern = Pattern.compile("(.*) 300w, (.*) 600w, (.*) 1000w, (.*) 2000w", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(srcset);

            while (matcher.find()) {
                img.attr("src", matcher.group(3));
                img.attr("srcset", "");
                img.attr("width", "100%");
            }
        }

        return document.outerHtml();
    }

    private void setArticleAuthorName(String name) {
        articleAuthorName.setText(name.toUpperCase());
    }

    private void setArticleAuthorImage(String url) {
        picasso.load(url).into(articleAuthorImage);
    }

}
