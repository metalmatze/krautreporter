package de.metalmatze.krautreporter.helpers;

import android.app.Application;

public class KrautreporterApplication extends Application {

    private static KrautreporterApplication instance;

    public static KrautreporterApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }
}
