package com.psy.places.webservice.wrapper;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sromanov on 15/2/2016.
 */
public class SafeBus extends Bus {

    private List<Object> mRegisteredObjects;

    public SafeBus(String identifier) {
        super(identifier);

        mRegisteredObjects = new ArrayList<>();
    }

    @Override
    public void register(Object object) {
        if (mRegisteredObjects.contains(object)) {
            return;
        }
        mRegisteredObjects.add(object);
        super.register(object);
    }

    public void unregister(Object object) {
        boolean contains = mRegisteredObjects.remove(object);
        if (!contains) {
            return;
        }

        super.unregister(object);
    }
}