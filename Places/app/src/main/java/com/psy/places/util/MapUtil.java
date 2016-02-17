package com.psy.places.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.psy.places.PlacesApplication;
import com.psy.places.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

/**
 * Created by sromanov on 16/2/2016.
 */
public class MapUtil {

    public static final String GOOGLE_MAPS_APP_PACKAGE = "com.google.android.apps.maps";

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

    private static final int KILOMETER = 1000;

    public static final void showOnMap(Context context, float lat, float lon, String name) {
        String placeName = new String();
        try {
            placeName = URLEncoder.encode(name, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
        }

        String uriFormat = String.format("geo:0:0?q=%1$f,%2$f%3$s&z=16", lat, lon, "(" + placeName + ")");
        Uri uri = Uri.parse(uriFormat);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(GOOGLE_MAPS_APP_PACKAGE);
        intent.setData(uri);
        if (intent.resolveActivity(PlacesApplication.getContext().getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static final boolean isGoogleMapsAppInstalled() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(GOOGLE_MAPS_APP_PACKAGE);
        return intent.resolveActivity(PlacesApplication.getContext().getPackageManager()) != null;
    }

    public static final String getDistanceLabel(float pureDistance) {
        int distance = Math.round(pureDistance);
        String distanceLabel = "";
        Context context = PlacesApplication.getContext();
        if (distance < KILOMETER) {
            distanceLabel = distance + " " + context.getString(R.string.meter_unit);
        } else if (distance % KILOMETER < 100) {
            int distanceKm = distance / KILOMETER;
            distanceLabel = distanceKm + " " + context.getString(R.string.kilometer_unit);
        } else {
            float distanceKm = (float) distance / (float) KILOMETER;
            distanceLabel = FORMATTER.format(distanceKm) + " " + context.getString(R.string.kilometer_unit);
        }
        return distanceLabel;
    }
}