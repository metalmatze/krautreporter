package de.metalmatze.krautreporter.helpers;

import android.app.Application;

import de.metalmatze.krautreporter.DaggerKrautreporterComponent;
import de.metalmatze.krautreporter.KrautreporterComponent;
import de.metalmatze.krautreporter.KrautreporterModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class KrautreporterApplication extends Application {

    public KrautreporterComponent krautreporterComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        this.krautreporterComponent = DaggerKrautreporterComponent.builder()
                .krautreporterModule(new KrautreporterModule(this))
                .build();

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);
    }

    public KrautreporterComponent getKrautreporterComponent() {
        return this.krautreporterComponent;
    }
}
