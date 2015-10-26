package de.metalmatze.krautreporter.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

public class Settings {
    public static final String LAST_ARTICLE_UPDATE = "lastArticlesUpdate";
    private final SharedPreferences preferences;

    public Settings(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Saves the current timestamp as last articles update.
     */
    public void setLastArticlesUpdate() {
        long time = new Date().getTime();
        preferences.edit()
                .putLong(LAST_ARTICLE_UPDATE, time)
                .apply();
    }

    /**
     * Returns the timestamp of the last articles update.
     *
     * @return Long
     */
    public Long getLastArticlesUpdate() {
        return preferences.getLong(LAST_ARTICLE_UPDATE, 0);
    }
}
