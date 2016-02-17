package com.psy.places.webservice.wrapper;

import com.android.volley.Request;
import com.psy.places.PlacesApplication;
import com.psy.places.util.ConnectivityUtil;
import com.psy.places.webservice.RequestManager;
import com.psy.places.webservice.wrapper.events.NoNetworkConnectionEvent;
import com.psy.places.webservice.wrapper.events.RequestCompleteEvent;
import com.psy.places.webservice.wrapper.events.RequestStartEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sromanov on 15/2/2016.
 */

public class BaseRequestWrapper {

    public static final RequestStartEvent START_EVENT = new RequestStartEvent();
    public static final RequestCompleteEvent COMPLETE_EVENT = new RequestCompleteEvent();
    public static final NoNetworkConnectionEvent NO_NETWORK_CONNECTION_EVENT = new NoNetworkConnectionEvent();

    private RequestManager mRequestManager;

    private Bus mWebserviceBus;

    private Object mListener;
    private List<Request<?>> mRequests;

    private boolean mCanBeProducer;

    private static Map<Class, Object> sProducers = new HashMap<>();

    public BaseRequestWrapper() {
        this(RequestManager.getInstance(), PlacesApplication.getWebServiceBus());
    }

    protected BaseRequestWrapper(RequestManager requestManager, Bus webserviceBus) {
        mRequestManager = requestManager;
        mWebserviceBus = webserviceBus;
        mRequests = new ArrayList<>();

        mCanBeProducer = false;
    }

    public void setListener(Object listener) {
        if (listener == null) {
            clearListener();
            return;
        }

        mListener = listener;
        if (mCanBeProducer) {
            Class[] producableEvents = getProducableEvents();
            for (Class event : producableEvents) {
                if (!isProducerForEventExists(event)) {
                    makeProducerForEvent(event);
                }
            }
        }

        mWebserviceBus.register(listener);
    }

    public static boolean isProducerForEventExists(Class event) {
        return sProducers.containsKey(event);
    }

    protected void makeRequest(Request<?> request) {
        makeRequest(request, true);
    }

    protected void makeRequest(Request<?> request, boolean shouldNotify) {
        if(!ConnectivityUtil.isNetworkConnected(PlacesApplication.getContext())) {
            mWebserviceBus.post(NO_NETWORK_CONNECTION_EVENT);
            return;
        }

        if (request != null) {
            mRequests.add(request);
            mRequestManager.makeRequest(request);
            if(shouldNotify) {
                mWebserviceBus.post(START_EVENT);
            }
        }
    }

    protected void postResponse(Object event) {
        mWebserviceBus.post(COMPLETE_EVENT);
        mWebserviceBus.post(event);
    }

    protected Class[] getProducableEvents() {
        return new Class[0];
    }

    protected boolean isProducerForEvent(Class event) {
        return sProducers.get(event) == this;
    }

    private void makeProducerForEvent(Class event) {
        Object previousProducer = sProducers.put(event, this);
        if (previousProducer != null) {
            throw new IllegalStateException("Previous producer exists for event: " + event.getSimpleName());
        }

        mWebserviceBus.register(this);
    }

    private void unmakeProducerForEvent(Class event) {
        sProducers.remove(event);
        mWebserviceBus.unregister(this);
    }

    private void clearListener() {
        cancelAllRequests();
        mRequests.clear();
        mWebserviceBus.unregister(mListener);
        if (mCanBeProducer) {
            Class[] producableEvents = getProducableEvents();
            for (Class event : producableEvents) {
                if (isProducerForEvent(event)) {
                    unmakeProducerForEvent(event);
                }
            }
        }
        mListener = null;
    }

    private void cancelAllRequests() {
        final List<Request<?>> requests = new ArrayList<>(mRequests);
        for (Request<?> request : requests) {
            request.cancel();
        }
    }
}