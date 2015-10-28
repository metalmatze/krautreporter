package de.metalmatze.krautreporter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.metalmatze.krautreporter.ActivityModule;
import de.metalmatze.krautreporter.KrautreporterComponent;
import de.metalmatze.krautreporter.KrautreporterModule;
import de.metalmatze.krautreporter.helpers.KrautreporterApplication;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getKrautreporterComponent().inject(this);
    }

    protected KrautreporterComponent getKrautreporterComponent() {
        return ((KrautreporterApplication) getApplication()).getKrautreporterComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    protected KrautreporterModule getKrautreporterModule() {
        return new KrautreporterModule(this.getApplication());
    }

}
