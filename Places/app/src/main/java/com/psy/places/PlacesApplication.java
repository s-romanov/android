package com.psy.places;

import android.app.Application;
import android.content.Context;

import com.psy.places.webservice.RequestManager;
import com.psy.places.webservice.wrapper.SafeBus;
import com.squareup.otto.Bus;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlacesApplication extends Application {

    private static final Bus UI_BUS = new Bus("ui");

    private static final SafeBus WEBSERVICE_BUS = new SafeBus("webservice");

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        RequestManager.initialize(getApplicationContext());
        WEBSERVICE_BUS.register(this);
    }

    public static Bus getUiBus() {
        return UI_BUS;
    }

    public static Bus getWebServiceBus() {
        return WEBSERVICE_BUS;
    }

    public static Context getContext() {
        return sContext;
    }
}