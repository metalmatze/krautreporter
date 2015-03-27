package de.metalmatze.krautreporter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getInstance(getActivity().getApplicationContext());

        if (getArguments().containsKey(ARTICLE_ID)) {
            article = realm
                        .where(Article.class)
                        .equalTo("id", getArguments().getInt(ARTICLE_ID))
                        .findFirst();
        }
        else {
            RealmResults<Article> articles = realm.where(Article.class).findAll();
            articles.sort("order", RealmResults.SORT_ORDER_DESCENDING);

            article = articles.first();
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

        if (article != null) {

            actionBarTitle.setActionBarTitle(article.getTitle());

            TextView articleTitle = (TextView) rootView.findViewById(R.id.article_title);
            TextView articleDate = (TextView) rootView.findViewById(R.id.article_date);
            TextView articleExcerpt = (TextView) rootView.findViewById(R.id.article_excerpt);
            TextView articleContent = (TextView) rootView.findViewById(R.id.article_content);

            articleTitle.setText(article.getHeadline());
            articleDate.setText(article.getDate());
            articleExcerpt.setText(article.getExcerpt());
            articleContent.setText(article.getContent());
        }

        return rootView;
    }
}
