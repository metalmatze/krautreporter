package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

        String uuid = this.getIntent().getStringExtra("uuid");
        Log.d("uuid in article activity", uuid);
        ArticleModel model = new Select().from(ArticleModel.class).where("uuid = ?", uuid).executeSingle();

        TextView articleTitle = (TextView) findViewById(R.id.article_title);
        TextView articleDate = (TextView) findViewById(R.id.article_date);
        ImageView articleImage = (ImageView) findViewById(R.id.image);
        TextView articleExcerpt = (TextView) findViewById(R.id.article_excerpt);
        TextView articleContent = (TextView) findViewById(R.id.article_content);

        articleTitle.setText(model.title);
        articleDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(model.date.getTime()));
        articleContent.setText(model.content);
    }

}
