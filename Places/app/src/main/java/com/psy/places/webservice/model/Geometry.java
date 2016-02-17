package com.psy.places.webservice.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sromanov on 15/2/2016.
 */
public class Geometry {

    @SerializedName("location")
    private PlaceLocation mLocation;

    public PlaceLocation getPlaceLocation() {
        return mLocation;
    }
}