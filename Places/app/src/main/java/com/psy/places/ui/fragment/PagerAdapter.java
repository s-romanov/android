package com.psy.places.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    ICurrentLocationHolder mLocationHolder;

    public PagerAdapter(FragmentManager fragmentManager, int NumOfTabs, ICurrentLocationHolder locationHolder) {
        super(fragmentManager);

        mNumOfTabs = NumOfTabs;
        mLocationHolder = locationHolder;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PlacesFragment placesListFragment = new PlacesFragment();
                placesListFragment.setLocationHolder(mLocationHolder);
                return placesListFragment;
            case 1:
                MapFragment mapFragment = new MapFragment();
                mapFragment.setLocationHolder(mLocationHolder);
                return mapFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}