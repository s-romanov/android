package com.psy.places.webservice.model;

import android.location.Location;
import android.location.LocationManager;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlaceLocation {

    @SerializedName("lat")
    private float mLatitude;

    @SerializedName("lng")
    private float mLongitude;

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        return location;
    }
}