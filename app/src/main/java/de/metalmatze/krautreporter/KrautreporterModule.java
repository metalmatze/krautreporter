package de.metalmatze.krautreporter;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.metalmatze.krautreporter.api.Api;

@Module
public class KrautreporterModule {

    private final Application application;

    public KrautreporterModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return this.application;
    }

    @Provides
    @Singleton
    Api provideApi() {
        return new Api(this.application);
    }

//    @Provides
//    @Singleton
//    AuthorService provideAuthorService() {
//        return new AuthorService(this.application);
//    }
//
//    @Provides
//    @Singleton
//    ArticleService provideArticleService() {
//        return new ArticleService(this.application);
//    }
//
//    @Provides
//    @Singleton
//    Settings provideSettings() {
//        return new Settings(this.application);
//    }
}
