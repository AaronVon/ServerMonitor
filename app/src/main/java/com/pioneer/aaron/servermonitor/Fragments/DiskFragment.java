package com.pioneer.aaron.servermonitor.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.pioneer.aaron.servermonitor.R;

/**
 * Created by Aaron on 6/12/15.
 */
public class DiskFragment extends Fragment {

    private ObservableScrollView mScrollView;

    public static DiskFragment newInstance() {
        return new DiskFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diskio_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.diskio_ScrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }
}
