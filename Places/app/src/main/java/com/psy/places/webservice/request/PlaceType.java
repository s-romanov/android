package com.psy.places.webservice.request;

/**
 * Created by sromanov on 15/2/2016.
 */
public enum PlaceType {

    BAR("bar"), FOOD("food");

    private String mPlaceTypeName;

    private PlaceType(String placeTypeName) {
        mPlaceTypeName = placeTypeName;
    }

    public String getName() {
        return mPlaceTypeName;
    }
}