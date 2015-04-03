package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.fragments.ArticleDetailFragment;
import de.metalmatze.krautreporter.fragments.ArticleListFragment;
import io.fabric.sdk.android.Fabric;

public class ArticleListActivity extends ActionBarActivity implements ArticleListFragment.OnItemSelectedCallback, ArticleDetailFragment.ActionBarTitle {

    protected ActionBar actionBar;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater transitionInflater = TransitionInflater.from(this);
            Transition transitionExit = transitionInflater.inflateTransition(R.transition.article_transition);

            getWindow().setExitTransition(transitionExit);
        }

        setContentView(R.layout.activity_article_list);

        if (findViewById(R.id.article_detail_container) != null) {
            twoPane = true;
        }

        actionBar = getSupportActionBar();
    }

    @Override
    public void onItemSelected(View view, int id) {
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setTransitionName("article_image");
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this, view, view.getTransitionName());

                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
