package com.psy.places.ui.events;

import android.location.Location;

/**
 * Created by sromanov on 17/2/2016.
 */
public class UserLocationChangedEvent {

    private Location mLocation;

    public UserLocationChangedEvent(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}