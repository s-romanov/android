package com.psy.places.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.psy.places.PlacesApplication;
import com.psy.places.R;
import com.psy.places.ui.adapter.PlacesAdapter;
import com.psy.places.ui.events.ErrorEvent;
import com.psy.places.ui.events.NoPlacesFoundEvent;
import com.psy.places.ui.events.UserLocationChangedEvent;
import com.psy.places.ui.tools.DisplayUtil;
import com.psy.places.ui.tools.VerticalSpaceItemDecoration;
import com.psy.places.util.IntentUtils;
import com.psy.places.util.MapUtil;
import com.psy.places.webservice.model.Place;
import com.psy.places.webservice.model.PlaceLocation;
import com.psy.places.webservice.model.PlacesResultsResponse;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;

/**
 * Created by sromanov on 15/2/2016.
 */
public class PlacesFragment extends BasePlacesContainerFragment {

    public static final NoPlacesFoundEvent NO_PLACES_FOUND_EVENT = new NoPlacesFoundEvent();
    public static final ErrorEvent ERROR_EVENT = new ErrorEvent();

    private static final int PLACES_SEPARATOR_HEIGHT = 20;

    @Bind(R.id.placesRecyclerView)
    RecyclerView mPlacesRecyclerView;

    @Bind(R.id.refreshBtn)
    Button mRefreshBtn;

    private PlacesAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_places;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlacesApplication.getUiBus().register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PlacesAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                int itemPosition = mPlacesRecyclerView.getChildAdapterPosition(clickedView);
                Place item = mAdapter.getItem(itemPosition);
                onPlaceClick(item);
            }
        });
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mPlacesRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration((int) DisplayUtil.dpToPx(getActivity(), PLACES_SEPARATOR_HEIGHT)));
        mPlacesRecyclerView.setAdapter(mAdapter);
        mAdapter.setCurrentLocation(mLocationHolder.getCurrentLocation());

        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IPlacesLoader) {
                    ((IPlacesLoader) getActivity()).loadPlaces();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        PlacesApplication.getUiBus().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onPlacesAvailable(PlacesResultsResponse response) {
        if (!response.isError()) {
            mRefreshBtn.setVisibility(View.GONE);
            loadPlaces(response.getPlaces());
        }
    }

    @Subscribe
    public void onUserLocationChanged(UserLocationChangedEvent event) {
        mAdapter.setCurrentLocation(event.getLocation());
    }

    @Subscribe
    public void onNoPlacesAvailable(NoPlacesFoundEvent event) {
        mRefreshBtn.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onNoPlacesAvailable(ErrorEvent event) {
        mRefreshBtn.setVisibility(View.VISIBLE);
    }

    private void onPlaceClick(Place place) {
        PlaceLocation placeLocation = place.getGeometry().getPlaceLocation();
        if (MapUtil.isGoogleMapsAppInstalled()) {
            MapUtil.showOnMap(getActivity(), placeLocation.getLatitude(), placeLocation.getLongitude(), place.getName());
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.google_maps_app_is_not_installed)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentUtils.openAppInGooglePlay(getActivity(), MapUtil.GOOGLE_MAPS_APP_PACKAGE);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void loadPlaces(List<Place> places) {
        mAdapter.append(places);
    }
}