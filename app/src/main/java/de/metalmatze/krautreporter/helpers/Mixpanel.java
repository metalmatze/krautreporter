package de.metalmatze.krautreporter.helpers;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import de.metalmatze.krautreporter.R;

public class Mixpanel {

    public static MixpanelAPI getInstance(Context context) {
        return MixpanelAPI.getInstance(context, context.getString(R.string.mixpanel_token));
    }

}
