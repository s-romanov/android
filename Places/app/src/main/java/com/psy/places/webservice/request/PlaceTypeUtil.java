package com.psy.places.webservice.request;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlaceTypeUtil {

    private static final String SEPARATOR = "|";

    public static final String collect(PlaceType[] placeTypes) {
        String result = new String();

        if (placeTypes == null) {
            return result;
        }

        int currentIndex = 0;

        for (PlaceType type : placeTypes) {
            result += type.getName();
            if (currentIndex < (placeTypes.length -1)) {
                result += SEPARATOR;
                currentIndex++;
            }
        }
        return result;
    }
}