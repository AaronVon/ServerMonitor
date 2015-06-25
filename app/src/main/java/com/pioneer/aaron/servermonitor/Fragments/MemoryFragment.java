package com.pioneer.aaron.servermonitor.Fragments;

import android.content.Context;
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
import com.pioneer.aaron.servermonitor.Adapters.ExpandableListAdapter;
import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.Helper.DynamicListviewUtil;
import com.pioneer.aaron.servermonitor.Helper.PrecisionFormat;
import com.pioneer.aaron.servermonitor.Helper.SystemTime;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.JsonUtilities.MemoryjsonParser;
import com.pioneer.aaron.servermonitor.MainActivity;
import com.pioneer.aaron.servermonitor.R;
import com.pioneer.aaron.servermonitor.SharedPres.SharedPres;
import com.pioneer.aaron.servermonitor.StatusAlert;
import com.pioneer.aaron.servermonitor.Widgets.AnimatedExpandableListView;
import com.pioneer.aaron.servermonitor.Constants.ExpandableListView_Helper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    //expandablelistview with animation
    private AnimatedExpandableListView listView;
    private ExpandableListAdapter adapter;
    private List<GroupItem> items = new ArrayList<>();
    private GroupItem groupItem = new GroupItem();

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

    private boolean instanceLoaded = false;
    private Context mContext;

    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.memory_layout,
                (ViewGroup) getActivity().findViewById(R.id.materialViewPager), false);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && instanceLoaded) {
            long period = new SharedPres(mContext).getInt(Constants.REFRESHGAP, 10) * 1000;

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            }, 0, period);
        }
        if (!isVisibleToUser && instanceLoaded) {
            mTimer.cancel();
        }
    }

    private void init() {
        wheelIndicatorView = (WheelIndicatorView) rootView.findViewById(R.id.memory_wheel_indicator_view);
        usedIndicatorItem = new WheelIndicatorItem(usedLoad, getResources().getColor(R.color.indicator_1));
        freeIndicatorItem = new WheelIndicatorItem(freeLoad, getResources().getColor(R.color.indicatior_2));
        wheelIndicatorView.addWheelIndicatorItem(usedIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(freeIndicatorItem);

        usedTextView = (TextView) rootView.findViewById(R.id.used_textView);
        freeTextView = (TextView) rootView.findViewById(R.id.free_textView);
        initListView();
        instanceLoaded = true;
    }

    private void initListView() {
        groupItem.title = "JSON META";
        items.add(groupItem);

        adapter = new ExpandableListAdapter(mContext);
        adapter.setData(items);

        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.memory_meta_listview);
        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                    DynamicListviewUtil.newInstance().resetHeight(listView, groupPosition);
                } else {
                    DynamicListviewUtil.newInstance().setExpandableListViewHeight(listView, groupPosition);
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
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
        updateListView(VALUE_START, jsonMETA);

        Map<String, Float> data = new HashMap<>();
        data = MemoryjsonParser.newInstance().parseJSON(jsonMETA);

        if (data.get("used") == null) {
            return;
        }

        usedLoad = data.get("used") / 1000;
        freeLoad = data.get("free") / 1000;

        usedTextView.setText("Used: " + PrecisionFormat.newInstance().shrink(usedLoad, 2) + "MB");
        freeTextView.setText("Free: " + PrecisionFormat.newInstance().shrink(freeLoad, 2) + "MB");

        if (usedLoad > 900.0) {
            new StatusAlert(mContext).showNotification(Constants.MEMORY_NOTIF, "RAM overload " + usedLoad + "MB");
        }

        wheelIndicatorView.setFilledPercent(100);
        usedIndicatorItem.setWeight(usedLoad);
        freeIndicatorItem.setWeight(freeLoad);
        wheelIndicatorView.notifyDataSetChanged();
        wheelIndicatorView.startItemsAnimation();
    }

    private void updateListView(String hint, String title) {
        ChildItem childItem = new ChildItem();
        childItem.title = title;
        childItem.hint = hint;

        groupItem.items.add(childItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.memory_ScrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }
}
