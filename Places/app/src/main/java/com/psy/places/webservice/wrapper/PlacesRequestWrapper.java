package com.psy.places.webservice.wrapper;

import android.location.Location;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.psy.places.webservice.model.PlacesResultsResponse;
import com.psy.places.webservice.request.NearbyPlacesRequest;
import com.psy.places.webservice.request.PlaceType;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlacesRequestWrapper extends BaseRequestWrapper {

    public void loadPlaces(Location location, int radius, PlaceType[] placeTypes) {
        makeRequest(new NearbyPlacesRequest(location.getLatitude(), location.getLongitude(), radius, placeTypes, new Response.Listener<PlacesResultsResponse>() {
            @Override
            public void onResponse(PlacesResultsResponse response) {
                if (response != null) {
                    postResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postResponse(new PlacesResultsResponse(error));
            }
        }));
    }
}