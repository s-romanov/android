package com.psy.places.ui.fragment;

/**
 * Created by sromanov on 16/2/2016.
 */
public abstract class BasePlacesContainerFragment extends BaseFragment {

    protected ICurrentLocationHolder mLocationHolder;

    public void setLocationHolder(ICurrentLocationHolder locationHolder) {
        mLocationHolder = locationHolder;
    }
}