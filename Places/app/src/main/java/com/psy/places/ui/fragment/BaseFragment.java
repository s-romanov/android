package com.psy.places.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by sromanov on 16/2/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected static final int NO_UI = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layout = getLayoutId();
        if (layout == NO_UI) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            View result = inflater.inflate(layout, container, false);
            ButterKnife.bind(this, result);
            return result;
        }
    }

    protected abstract int getLayoutId();
}