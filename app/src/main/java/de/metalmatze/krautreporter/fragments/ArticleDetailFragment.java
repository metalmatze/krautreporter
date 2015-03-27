package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.Article;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleDetailFragment extends Fragment {

    public interface ActionBarTitle {
        public void setActionBarTitle(String title);
    }

    public static final String ARTICLE_ID = "article_id";

    private Article article;
    private ActionBarTitle actionBarTitle;

    @InjectView(R.id.article_title) TextView articleTitle;
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        actionBarTitle = (ActionBarTitle) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_article, container, false);

        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);

        if (article != null) {

            actionBarTitle.setActionBarTitle(article.getTitle());

            articleTitle.setText(article.getHeadline());
            articleDate.setText(article.getDate());
            articleExcerpt.setText(article.getExcerpt());
            articleContent.setText(article.getContent());
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_article, menu);
    }

    
}
