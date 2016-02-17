package com.psy.places.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.psy.places.PlacesApplication;
import com.psy.places.R;
import com.psy.places.ui.tools.DisplayUtil;
import com.psy.places.util.MapUtil;
import com.psy.places.webservice.model.Place;
import com.psy.places.webservice.model.PlaceLocation;
import com.psy.places.webservice.model.PlacesResultsResponse;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sromanov on 15/2/2016.
 */
public class MapFragment extends BasePlacesContainerFragment {

    private Map<Marker, Place> mPlacesMap = new HashMap<>();

    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mIsMapDrawn;
    private PlacesResultsResponse mResponse;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlacesApplication.getUiBus().register(this);
    }

    @Override
    public void onDestroy() {
        PlacesApplication.getUiBus().unregister(this);
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Subscribe
    public void onPlacesAvailable(PlacesResultsResponse response) {
        if (!response.isError()) {
            if (mIsMapDrawn) {
                onResponseAvailable(response);
            } else {
                mResponse = response;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mIsMapDrawn = true;
                setupMap();
                if (mResponse != null) {
                    onResponseAvailable(mResponse);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    private void setupMap() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View layout = getActivity().getLayoutInflater().inflate(R.layout.view_marker_info_window, null);
                Place place = mPlacesMap.get(marker);
                if (place != null) {
                    String placeName = place.getName();
                    PlaceLocation placeLocation = place.getGeometry().getPlaceLocation();

                    TextView placeNameTv = (TextView) layout.findViewById(R.id.placeNameTv);
                    placeNameTv.setText(placeName);

                    TextView distanceTv = (TextView) layout.findViewById(R.id.placeDistanceTv);
                    float pureDistance = mLocationHolder.getCurrentLocation().distanceTo(placeLocation.getLocation());
                    String distanceLabel = MapUtil.getDistanceLabel(pureDistance);
                    distanceTv.setText(distanceLabel);
                }
                return layout;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                return false;
            }
        });
    }

    private void clearPlaces() {
        Iterator<Map.Entry<Marker, Place>> iterator = mPlacesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Marker, Place> entry = iterator.next();
            entry.getKey().remove();
            iterator.remove();
        }
    }

    private void addPlaces(List<Place> places) {
        clearPlaces();
        for (Place place : places) {
            addMarker(place);
        }
    }

    private Marker addMarker(Place place) {
        MarkerOptions markerOptions = new MarkerOptions();
        PlaceLocation placeLocation = place.getGeometry().getPlaceLocation();
        markerOptions.position(new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude()));

        Marker marker = null;
        if (isAdded() && !isDetached()) {
            marker = mMap.addMarker(markerOptions);
            mPlacesMap.put(marker, place);
        }

        return marker;
    }

    private void onResponseAvailable(PlacesResultsResponse response) {
        addPlaces(response.getPlaces());
        updateMapBounds();
    }

    private void updateMapBounds() {
        if (!mIsMapDrawn) {
            return;
        }

        Set<Marker> markers = mPlacesMap.keySet();
        if (markers.isEmpty()) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        int mapPadding = (int) DisplayUtil.dpToPx(PlacesApplication.getContext(),PlacesApplication.getContext().getResources().getInteger(R.integer.map_padding));
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, mapPadding);
        mMap.animateCamera(cu);
    }
}