package com.psy.places.webservice.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.psy.places.PlacesApplication;
import com.psy.places.R;
import com.psy.places.webservice.model.PlacesResultsResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sromanov on 15/2/2016.
 */
public class NearbyPlacesRequest extends BaseJsonRequest<PlacesResultsResponse> {

    private static String[] paths = {"maps", "api", "place", "nearbysearch", "json"};

    private static final String LOCATION_FORMAT = "%f,%f";

    private static String LOCATION = "location";
    private static String RADIUS = "radius";
    private static String TYPES = "types";
    private static String KEY = "key";

    public NearbyPlacesRequest(double latitude, double longitude, int radius, PlaceType[] placeTypes, Response.Listener<PlacesResultsResponse> aListener, Response.ErrorListener aErrorListener) {
        super(Request.Method.GET, paths, getParams(latitude, longitude, radius, placeTypes), null, aListener, aErrorListener);
    }

    @Override
    protected Type getResultType() {
        return new TypeToken<PlacesResultsResponse>() {
        }.getType();
    }

    private final static Map<String, String> getParams(double latitude, double longitude, int radius, PlaceType[] placeTypes) {
        Map<String, String> params = new HashMap<>();
        params.put(LOCATION, String.format(LOCATION_FORMAT, latitude, longitude));
        params.put(RADIUS, String.valueOf(radius));
        params.put(TYPES, PlaceTypeUtil.collect(placeTypes));
        params.put(KEY, PlacesApplication.getContext().getString(R.string.places_api_key));
        return params;
    }
}