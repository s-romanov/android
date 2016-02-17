package com.psy.places.ui.adapter;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.psy.places.R;
import com.psy.places.util.MapUtil;
import com.psy.places.webservice.model.Place;
import com.psy.places.webservice.model.PlaceLocation;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sromanov on 16/2/2016.
 */
public class PlaceViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.placeNameTv)
    TextView mPlaceNameTv;

    @Bind(R.id.placeDistanceTv)
    TextView mPlaceDistanceTv;

    View mItemView;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        mItemView = itemView;
    }

    public void update(Place place, Location currentLocation) {
        mPlaceNameTv.setText(place.getName());
        PlaceLocation placeLocation = place.getGeometry().getPlaceLocation();
        float pureDistance = currentLocation.distanceTo(placeLocation.getLocation());
        String distanceLabel = MapUtil.getDistanceLabel(pureDistance);
        mPlaceDistanceTv.setText(distanceLabel);
    }
}