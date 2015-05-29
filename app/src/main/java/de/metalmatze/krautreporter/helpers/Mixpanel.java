package de.metalmatze.krautreporter.helpers;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import de.metalmatze.krautreporter.BuildConfig;
import de.metalmatze.krautreporter.R;

public class Mixpanel {

    public static MixpanelAPI getInstance(Context context) {

        String token = context.getString(R.string.mixpanel_token);

        if (BuildConfig.DEBUG) {
            token = "435f4592acdcafa38ea5378f5b1d4d77"; // md5("mixpanel")
        }

        return MixpanelAPI.getInstance(context, token);
    }

}
