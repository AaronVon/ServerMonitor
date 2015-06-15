package com.pioneer.aaron.servermonitor.Fragments;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.Helper.PrecisionFormat;
import com.pioneer.aaron.servermonitor.Helper.SystemTime;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.JsonUtilities.MemoryjsonParser;
import com.pioneer.aaron.servermonitor.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aaron on 6/12/15.
 */
public class MemoryFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private View rootView;

    private static String VALUE_START = "";
    private static String VALUE_END = "";
    private float usedLoad, freeLoad;

    private WheelIndicatorView wheelIndicatorView;
    private WheelIndicatorItem usedIndicatorItem;
    private WheelIndicatorItem freeIndicatorItem;

    private TextView usedTextView;
    private TextView freeTextView;
    private TextView loggingTextView;

    private Timer mTimer = new Timer();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUI();
            }
        }
    };


    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.memory_layout, container, false);

        init();

        return rootView;
    }

    private void init() {
        wheelIndicatorView = (WheelIndicatorView) rootView.findViewById(R.id.memory_wheel_indicator_view);
        usedIndicatorItem = new WheelIndicatorItem(usedLoad, getResources().getColor(R.color.indicator_1));
        freeIndicatorItem = new WheelIndicatorItem(freeLoad, getResources().getColor(R.color.indicatior_2));
        wheelIndicatorView.addWheelIndicatorItem(usedIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(freeIndicatorItem);

        usedTextView = (TextView) rootView.findViewById(R.id.used_textView);
        freeTextView = (TextView) rootView.findViewById(R.id.free_textView);
        loggingTextView = (TextView) rootView.findViewById(R.id.memory_info);

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
            params.put(Constants.KEY_KEYS, Constants.MEMORY_VALUE_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonMETA = new JsonHttpUtil().getJsonMETA(Constants.POST_URL, params);

        Map<String, Float> data = new HashMap<>();
        data = MemoryjsonParser.newInstance().parseJSON(jsonMETA);

        if (data.get("used") == null) {
            return;
        }

        usedLoad = data.get("used") / 1000;
        freeLoad = data.get("free") / 1000;

        usedTextView.setText("Used: " + PrecisionFormat.newInstance().shrink(usedLoad, 2) + "MB");
        freeTextView.setText("Free: " + PrecisionFormat.newInstance().shrink(freeLoad, 2) + "MB");
        loggingTextView.setText(jsonMETA);

        wheelIndicatorView.setFilledPercent(100);
        usedIndicatorItem.setWeight(usedLoad);
        freeIndicatorItem.setWeight(freeLoad);
        wheelIndicatorView.notifyDataSetChanged();
        wheelIndicatorView.startItemsAnimation();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.memory_ScrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }
}
