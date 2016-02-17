package com.psy.places.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.psy.places.PlacesApplication;
import com.psy.places.R;
import com.psy.places.ui.events.UserLocationChangedEvent;
import com.psy.places.ui.fragment.ICurrentLocationHolder;
import com.psy.places.ui.fragment.IPlacesLoader;
import com.psy.places.ui.fragment.PagerAdapter;
import com.psy.places.ui.fragment.PlacesFragment;
import com.psy.places.util.DialogUtil;
import com.psy.places.webservice.model.PlacesResultsResponse;
import com.psy.places.webservice.request.PlaceType;
import com.psy.places.webservice.wrapper.PlacesRequestWrapper;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

import static com.google.android.gms.common.GoogleApiAvailability.getInstance;

public class MainActivity extends BaseActivity implements ICurrentLocationHolder, IPlacesLoader, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    private static final int REQUEST_CODE_LOCATION = 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private static final String DIALOG_ERROR = "dialog_error";
    private static final String LAST_LOCATION_KEY = "last_location_key";

    private static int UPDATE_INTERVAL = 10000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 30;

    @Bind(R.id.main_layout)
    RelativeLayout mMainLayout;

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private boolean mAreLocationUpdatesStarted;
    private LocationRequest mLocationRequest;

    private PlacesRequestWrapper mRequestWrapper;
    private PagerAdapter mPagerAdapter;

    private PlacesResultsResponse mResponse;

    private boolean mResolvingError;

    protected LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabLayout.addTab(mTabLayout.newTab().setText("Bars List"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Map"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mRequestWrapper = new PlacesRequestWrapper();
        PlacesApplication.getWebServiceBus().register(this);
        mRequestWrapper.setListener(this);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        if (mGoogleApiClient.isConnected() && !mAreLocationUpdatesStarted) {
            mAreLocationUpdatesStarted = true;
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putParcelable(LAST_LOCATION_KEY, mLastLocation);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Location location = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            if (location != null) {
                mLastLocation = location;
                PlacesApplication.getUiBus().post(new UserLocationChangedEvent(mLastLocation));
            }
        }
    }

    @Override
    public void onDestroy() {
        PlacesApplication.getWebServiceBus().unregister(this);
        mRequestWrapper.setListener(null);
        super.onDestroy();
    }

    @Subscribe
    public void onPlacesResponse(PlacesResultsResponse response) {
        if (!response.isError()) {
            mResponse = response;
            switch (mResponse.getStatus()) {
                case OK:
                    PlacesApplication.getUiBus().post(response);
                    break;
                case OVER_QUERY_LIMIT:
                    DialogUtil.showAlertMessage(this, R.string.query_limit_reached);
                    break;
                case REQUEST_DENIED:
                    DialogUtil.showAlertMessage(this, R.string.invalid_request);
                    break;
                case ZERO_RESULTS:
                    DialogUtil.showAlertMessage(this, R.string.no_results);
                    PlacesApplication.getUiBus().post(PlacesFragment.NO_PLACES_FOUND_EVENT);
                    break;
                case INVALID_REQUEST:
                    DialogUtil.showAlertMessage(this, R.string.invalid_request);
                    break;
                default:
                    break;
            }
        } else {
            DialogUtil.showAlertMessage(this, R.string.error);
            PlacesApplication.getUiBus().post(PlacesFragment.ERROR_EVENT);
        }
    }

    @Override
    protected void onNoNetworkConnection() {
        super.onNoNetworkConnection();

        PlacesApplication.getUiBus().post(PlacesFragment.NO_PLACES_FOUND_EVENT);
    }

    @Override
    public Location getCurrentLocation() {
        return mLastLocation;
    }

    @Override
    public void loadPlaces() {
        mRequestWrapper.loadPlaces(getCurrentLocation(), getResources().getInteger(R.integer.search_radius), new PlaceType[]{PlaceType.BAR});
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        handleRecentLocation();

        if (!mAreLocationUpdatesStarted) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            handleRecentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleRecentLocation();
                } else {
                    onLocationPermissionsDenied();
                }
                return;
            }
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                showProgress();
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        onLocationPermissionsDenied();
                        break;
                }
                break;
        }
    }

    private void handleRecentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null && location != mLastLocation) {
                hideProgress();
                if (mLastLocation != null && location.distanceTo(mLastLocation) < DISPLACEMENT) {
                    return;
                }
                setLastLocation(location);
                loadPlaces();
            }
        }
    }

    private void setLastLocation(Location location) {
        mLastLocation = location;
        PlacesApplication.getUiBus().post(new UserLocationChangedEvent(location));
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), DIALOG_ERROR);
    }

    private void onLocationPermissionsDenied() {
        Snackbar.make(mMainLayout, R.string.no_permissions, Snackbar.LENGTH_LONG).show();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        );
        result.setResultCallback(this);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
}