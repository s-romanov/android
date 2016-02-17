package com.psy.places.ui.tools;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by sromanov on 21.5.2015 г..
 */
public class DisplayUtil {

    public static float dpToPx(final Context context, final float dp) {
        return dp / context.getResources().getDisplayMetrics().density;
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}