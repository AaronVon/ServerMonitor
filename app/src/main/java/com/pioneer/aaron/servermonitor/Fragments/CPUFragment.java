package com.pioneer.aaron.servermonitor.Fragments;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.Helper.PrecisionFormat;
import com.pioneer.aaron.servermonitor.Helper.SystemTime;
import com.pioneer.aaron.servermonitor.JsonUtilities.CPUjsonParser;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aaron on 6/12/15.
 */
public class CPUFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private View rootView;
    //params
    private static String VALUE_START = "";
    private static String VALUE_END = "";

    private WheelIndicatorView wheelIndicatorView;
    private WheelIndicatorItem systemIndicatorItem;
    private WheelIndicatorItem waitIndicatorItem;
    private WheelIndicatorItem userIndicatorItem;
    private float systemLoad, waitLoad, userLoad;

    private TextView loggingTextView;
    private TextView systemTextView;
    private TextView userTextView;
    private TextView waitTextView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUI();
            }
        }
    };

    private Timer mTimer = new Timer();

    public static CPUFragment newInstance() {
        return new CPUFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cpu_layout, container, false);

        init();

        return rootView;
    }

    private void init() {

        wheelIndicatorView = (WheelIndicatorView) rootView.findViewById(R.id.cpu_wheel_indicator_view);
        systemIndicatorItem = new WheelIndicatorItem(systemLoad, getResources().getColor(R.color.indicator_1));
        userIndicatorItem = new WheelIndicatorItem(userLoad, getResources().getColor(R.color.indicatior_2));
        waitIndicatorItem = new WheelIndicatorItem(waitLoad, getResources().getColor(R.color.indicator_3));
        wheelIndicatorView.addWheelIndicatorItem(systemIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(userIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(waitIndicatorItem);

        loggingTextView = (TextView) rootView.findViewById(R.id.cpu_info);
        systemTextView = (TextView) rootView.findViewById(R.id.system_textView);
        userTextView = (TextView) rootView.findViewById(R.id.user_textView);
        waitTextView = (TextView) rootView.findViewById(R.id.wait_textView);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 0, 10000/*server update data every 5 min*/);
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
            params.put(Constants.KEY_KEYS, Constants.CPU_VALUE_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonMETA = new JsonHttpUtil().getJsonMETA(Constants.POST_URL, params);

        Map<String, Float> data = new HashMap<>();
        data = CPUjsonParser.newInstance().parseJSON(jsonMETA);

        //handle empty data response
        if (data.get("system") == null) {
            return;
        }

        systemLoad =  data.get("system") * 10;
        userLoad = data.get("user") * 10;
        waitLoad = data.get("wait") * 10;
        Log.d("system", systemLoad + "");
        Log.d("user", userLoad + "");
        Log.d("wait", waitLoad + "");

        loggingTextView.setText(jsonMETA);
        systemTextView.setText("System " + PrecisionFormat.newInstance().shrink(systemLoad, 2) + "%");
        userTextView.setText("User " + PrecisionFormat.newInstance().shrink(userLoad, 2) + "%");
        waitTextView.setText("Wait " + PrecisionFormat.newInstance().shrink(waitLoad, 2) + "%");

        int percentageInAll = (int) (systemLoad + userLoad + waitLoad);

        systemIndicatorItem.setWeight(systemLoad);
        userIndicatorItem.setWeight(userLoad);
        waitIndicatorItem.setWeight(waitLoad);

        wheelIndicatorView.setFilledPercent(percentageInAll);
        wheelIndicatorView.notifyDataSetChanged();
        wheelIndicatorView.startItemsAnimation();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.cpu_ScrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    @Override
    public void onStop() {
        mTimer.cancel();
        super.onStop();
    }
}
