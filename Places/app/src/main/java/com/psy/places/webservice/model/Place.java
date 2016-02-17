package com.psy.places.webservice.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sromanov on 15/2/2016.
 */
public class Place {

    @SerializedName("name")
    private String mName;

    @SerializedName("geometry")
    private Geometry mGeometry;

    public String getName() {
        return mName;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }
}