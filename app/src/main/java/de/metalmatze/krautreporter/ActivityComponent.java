package de.metalmatze.krautreporter;

import android.app.Activity;

import dagger.Component;

@PerActivity
@Component(dependencies = {KrautreporterComponent.class}, modules = {ActivityModule.class})
public interface ActivityComponent {
    Activity activity();
}
