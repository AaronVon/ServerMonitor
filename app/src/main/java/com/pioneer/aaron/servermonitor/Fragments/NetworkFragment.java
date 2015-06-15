package com.pioneer.aaron.servermonitor.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.Helper.SystemTime;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aaron on 6/12/15.
 */
public class NetworkFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private View rootView;

    private static String VALUE_START = "";
    private static String VALUE_END = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUI();
            }
        }
    };

    private Timer mTimer;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.network_layout, container, false);

        init();
        return rootView;
    }

    private void init() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 20, 10000);
    }

    private void updateUI() {
        Map<String, String> params = new HashMap<>();
        try {
            params.put(Constants.KEY_API, Constants.VALUE_API);
            VALUE_START = SystemTime.newInstance().getStart();
            VALUE_END = SystemTime.newInstance().getEnd();
            params.put(Constants.KEY_START, VALUE_START);
            params.put(Constants.KEY_END, VALUE_END);
            params.put(Constants.KEY_ACTION, Constants.VALUE_ACTION);
            params.put(Constants.KEY_KEYS, Constants.NETWORK_VALUE_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonMETA = new JsonHttpUtil().getJsonMETA(Constants.POST_URL, params);
        Log.d("JSONMEtA", jsonMETA);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.network_ScrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

}
