package com.psy.places.ui.adapter;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.psy.places.R;
import com.psy.places.webservice.model.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sromanov on 16/2/2016.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private List<Place> mPlaces = new ArrayList<>();
    private Location mCurrentLocation;
    private View.OnClickListener mOnClickListener;

    public PlacesAdapter(View.OnClickListener listener) {
        this(new ArrayList<Place>(), listener);
    }

    public PlacesAdapter(List<Place> places, View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setCurrentLocation(Location location) {
        mCurrentLocation = location;
        notifyDataSetChanged();
    }

    public Place getItem(int itemIndex) {
        if(itemIndex >= mPlaces.size()) {
            return null;
        }
        return mPlaces.get(itemIndex);
    }

    public void append(List<Place> places) {
        mPlaces.clear();
        Collections.sort(places, new PlaceComparator(mCurrentLocation));
        mPlaces.addAll(places);
        notifyDataSetChanged();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_place_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.update(place, mCurrentLocation);
    }

    @Override
    public int getItemCount() {
        return (mPlaces != null ? mPlaces.size() : 0);
    }

    private class PlaceComparator  implements Comparator<Place> {

        Location mCurrentLocation;

        public PlaceComparator(Location location) {
            mCurrentLocation = location;
        }

        @Override
        public int compare(Place lPlace, Place rPlace) {
            Location leftPlaceLocation = lPlace.getGeometry().getPlaceLocation().getLocation();
            int leftLocationDistance = (int) mCurrentLocation.distanceTo(leftPlaceLocation);

            Location rightPlaceLocation = rPlace.getGeometry().getPlaceLocation().getLocation();
            int rightLocationDistance = (int) mCurrentLocation.distanceTo(rightPlaceLocation);

            return leftLocationDistance - rightLocationDistance;
        }
    }
}