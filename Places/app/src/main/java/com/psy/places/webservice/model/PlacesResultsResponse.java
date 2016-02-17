package com.psy.places.webservice.model;

import com.android.volley.VolleyError;
import com.google.gson.annotations.SerializedName;
import com.psy.places.webservice.response.BaseJsonResponse;

import java.util.List;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlacesResultsResponse extends BaseJsonResponse {

    public static enum Status {

        OK, ZERO_RESULTS, OVER_QUERY_LIMIT, REQUEST_DENIED, INVALID_REQUEST, NO_STATUS;

        private Status() {
        }

        public static Status generate(String statusMsg) {
            switch (statusMsg) {
                case "OK":
                    return OK;
                //    OK indicates that no errors occurred; the place was successfully detected and at least one result was returned.

                case "ZERO_RESULTS":
                    return ZERO_RESULTS;
                //    ZERO_RESULTS indicates that the search was successful but returned no results. This may occur if the search was passed a latlng in a remote location.

                case "OVER_QUERY_LIMIT":
                    return OVER_QUERY_LIMIT;
                //    OVER_QUERY_LIMIT indicates that you are over your quota.

                case "REQUEST_DENIED":
                    return REQUEST_DENIED;
                //    REQUEST_DENIED indicates that your request was denied, generally because of lack of an invalid key parameter.

                case "INVALID_REQUEST":
                    return INVALID_REQUEST;
                default:
                    return NO_STATUS;
            }
        }
    }

    @SerializedName("results")
    private List<Place> mPlaces;

    @SerializedName("status")
    public String mStatus;

    public PlacesResultsResponse(VolleyError error) {
        super(error);
    }

    public List<Place> getPlaces() {
        return mPlaces;
    }

    public Status getStatus() {
        return Status.generate(mStatus);
    }
}