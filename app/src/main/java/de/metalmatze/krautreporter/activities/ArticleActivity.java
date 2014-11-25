package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;

import de.metalmatze.krautreporter.R;
import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_article);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra("id", -1);
        ArticleModel articleModel = new Select().from(ArticleModel.class).where("id = ?", id).executeSingle();

        TextView articleTitle = (TextView) findViewById(R.id.article_title);
        TextView articleDate = (TextView) findViewById(R.id.article_date);
        ImageView articleImage = (ImageView) findViewById(R.id.image);
        TextView articleExcerpt = (TextView) findViewById(R.id.article_excerpt);
        TextView articleContent = (TextView) findViewById(R.id.article_content);

        getSupportActionBar().setTitle(articleModel.title);

        articleTitle.setText(articleModel.title);
        articleDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(articleModel.date.getTime()));
        articleContent.setText(Html.fromHtml(articleModel.content));
    }

}
