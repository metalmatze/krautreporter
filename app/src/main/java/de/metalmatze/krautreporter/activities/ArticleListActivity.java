package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.api.Api;
import de.metalmatze.krautreporter.fragments.ArticleDetailFragment;
import de.metalmatze.krautreporter.fragments.ArticleListFragment;
import io.realm.Realm;

public class ArticleListActivity extends ActionBarActivity implements ArticleListFragment.Callbacks, ArticleDetailFragment.ActionBarTitle {

    protected ActionBar actionBar;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_list);

        if (findViewById(R.id.article_detail_container) != null) {
            twoPane = true;
        }

        actionBar = getSupportActionBar();

        Realm realm = Realm.getInstance(getApplicationContext());

        Api.with(realm).updateAuthors();
        Api.with(realm).updateArticles();

        Toast.makeText(getApplicationContext(), "Fetched new authors & articles", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(int id) {
        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(ArticleDetailFragment.ARTICLE_ID, id);

            ArticleDetailFragment fragment = new ArticleDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.article_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailFragment.ARTICLE_ID, id);

            startActivity(intent);
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
