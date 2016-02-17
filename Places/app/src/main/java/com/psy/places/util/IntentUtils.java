package com.psy.places.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by sromanov on 16/2/2016.
 */
public class IntentUtils {

    private static final String PLAY_APP_URL = "market://details?id=%1$s";
    private static final String PLAY_APP_HTTP_URL = "http://play.google.com/store/apps/details?id=%1$s";

    public static final void openAppInGooglePlay(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(PLAY_APP_URL, appPackageName))));
        } catch (android.content.ActivityNotFoundException ex) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(PLAY_APP_HTTP_URL, appPackageName))));
        }
    }
}