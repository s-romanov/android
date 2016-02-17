package com.psy.places.webservice.response;

import com.android.volley.VolleyError;

/**
 * Created by sromanov on 15/2/2016.
 */
public class BaseJsonResponse {

    private VolleyError mVolleyError;

    public BaseJsonResponse() {
        this(null);
    }

    public BaseJsonResponse(VolleyError error) {
        mVolleyError = error;
    }

    public boolean isError() {
        return mVolleyError != null;
    }

    public VolleyError getError() {
        return mVolleyError;
    }
}