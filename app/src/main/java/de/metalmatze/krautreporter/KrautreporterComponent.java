package de.metalmatze.krautreporter;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import de.metalmatze.krautreporter.activities.BaseActivity;
import de.metalmatze.krautreporter.api.Api;

@Singleton
@Component(modules = KrautreporterModule.class)
public interface KrautreporterComponent {

    void inject(BaseActivity activity);

    Context context();

    Api api();
}
