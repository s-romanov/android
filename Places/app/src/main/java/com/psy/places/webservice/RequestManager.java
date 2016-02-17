package com.psy.places.webservice;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sromanov on 15/2/2016.
 */
public class RequestManager {

    private static final int SOCKET_TIMEOUT_MS = 30000;

    private static RequestManager sInstance;
    private static boolean sIsInitialized;

    public static void initialize(Context context) {
        sInstance = new RequestManager(context);
        sIsInitialized = true;
    }

    public static RequestManager getInstance() {
        if (!sIsInitialized) {
            throw new RuntimeException("RequestManager is not initalized.");
        }
        return sInstance;
    }

    private Context mContext;
    private RequestQueue mRequestQueue;

    private RequestManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.start();
    }

    public RequestManager() {

    }

    public void makeRequest(Request request) {
        request.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public void cancelRequest(final Request request) {
        request.cancel();
    }
}
