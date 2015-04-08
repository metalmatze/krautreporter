package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.fragments.ArticleDetailFragment;
import de.metalmatze.krautreporter.fragments.ArticleListFragment;
import de.metalmatze.krautreporter.helpers.Mixpanel;
import io.fabric.sdk.android.Fabric;

public class ArticleListActivity extends ActionBarActivity implements ArticleListFragment.FragmentCallback, ArticleDetailFragment.ActionBarTitle {

    public static final String LOG_TAG = ArticleListActivity.class.getSimpleName();

    protected ActionBar actionBar;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_article_list);

        if (findViewById(R.id.article_detail_container) != null) {
            twoPane = true;
        }

        actionBar = getSupportActionBar();
    }

    @Override
    protected void onDestroy() {
        Mixpanel.getInstance(this).flush();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_become_member) {
            Mixpanel.getInstance(this).track(getString(R.string.mixpanel_become_member), null);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://krautreporter.de/pages/mitglied_werden"));

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
    public boolean isTwoPane() {
        return twoPane;
    }

    @Override
    public void setActionBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
