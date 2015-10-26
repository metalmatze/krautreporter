package de.metalmatze.krautreporter.helpers;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class KrautreporterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.getInstance(configuration);
    }
}
