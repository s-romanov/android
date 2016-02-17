package com.psy.places.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.psy.places.PlacesApplication;
import com.psy.places.util.DialogUtil;
import com.psy.places.webservice.wrapper.events.NoNetworkConnectionEvent;
import com.psy.places.webservice.wrapper.events.RequestCompleteEvent;
import com.psy.places.webservice.wrapper.events.RequestStartEvent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by sromanov on 16/2/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final RequestListener mRequestListener = new RequestListener();

    private ProgressDialog mProgressDialog;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldSetupUi()) {
            int layoutId = getLayoutId();
            setContentView(layoutId);
            ButterKnife.bind(this);
        }
    }

    @Override
    protected void onStop() {
        PlacesApplication.getWebServiceBus().unregister(mRequestListener);

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        PlacesApplication.getWebServiceBus().register(mRequestListener);
    }

    protected boolean shouldSetupUi() {
        return true;
    }

    protected void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void onNoNetworkConnection() {
    }

    private class RequestListener {

        @Subscribe
        public void onNoNetworkConnectionEvent(NoNetworkConnectionEvent event) {
            DialogUtil.showMissingInternetAlert(BaseActivity.this);
            onNoNetworkConnection();
        }

        @Subscribe
        public void onRequestStart(RequestStartEvent event) {
            showProgress();
        }

        @Subscribe
        public void onRequestComplete(RequestCompleteEvent event) {
            hideProgress();
        }
    }
}