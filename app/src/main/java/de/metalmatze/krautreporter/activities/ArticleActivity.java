package de.metalmatze.krautreporter.activities;

import android.app.Activity;
import android.os.Bundle;

import de.metalmatze.krautreporter.R;

public class ArticleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_article);
    }

}
