package de.metalmatze.krautreporter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.fragments.ArticleDetailFragment;

public class ArticleDetailActivity extends AppCompatActivity implements ArticleDetailFragment.ActionBarTitle {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(ArticleDetailFragment.ARTICLE_ID,
                    getIntent().getIntExtra(ArticleDetailFragment.ARTICLE_ID, -1));

            ArticleDetailFragment fragment = new ArticleDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.article_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        if (id == R.id.action_become_member) {
            Answers.getInstance().logCustom(
                    new CustomEvent(getString(R.string.answers_become_member))
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://krautreporter.de/pages/mitglied_werden"));

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setActionBarTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
