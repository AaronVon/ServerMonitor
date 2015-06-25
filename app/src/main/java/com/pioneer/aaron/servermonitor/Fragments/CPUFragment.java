package com.pioneer.aaron.servermonitor.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.pioneer.aaron.servermonitor.JsonUtilities.CPUjsonParser;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
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

    private TextView systemTextView;
    private TextView userTextView;
    private TextView waitTextView;

    //expandable list view
    private AnimatedExpandableListView listView;
    private ExpandableListAdapter adapter;
    private List<GroupItem> items = new ArrayList<>();
    private GroupItem groupItem = new GroupItem();

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
    private boolean instanceLoaded= false;
    private Context mContext;

    public static CPUFragment newInstance() {
        return new CPUFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize fragments
        mContext = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.cpu_layout,
                (ViewGroup) getActivity().findViewById(R.id.materialViewPager), false);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    //have to use handler to send message to the activity's original thread to deal with the ui components
                    //or will get exceptions
                    mHandler.sendEmptyMessage(0);
                    Log.d("cpufragment", "refresh ui");
                }
            }, 0, period);
        }
        if (!isVisibleToUser && instanceLoaded) {
            mTimer.cancel();
            Log.d("cpufragment", "thread shut down");
        }
    }

    private void init() {

        wheelIndicatorView = (WheelIndicatorView) rootView.findViewById(R.id.cpu_wheel_indicator_view);
        systemIndicatorItem = new WheelIndicatorItem(systemLoad, getResources().getColor(R.color.indicator_1));
        userIndicatorItem = new WheelIndicatorItem(userLoad, getResources().getColor(R.color.indicatior_2));
        waitIndicatorItem = new WheelIndicatorItem(waitLoad, getResources().getColor(R.color.indicator_3));
        wheelIndicatorView.addWheelIndicatorItem(systemIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(userIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(waitIndicatorItem);

        systemTextView = (TextView) rootView.findViewById(R.id.system_textView);
        userTextView = (TextView) rootView.findViewById(R.id.user_textView);
        waitTextView = (TextView) rootView.findViewById(R.id.wait_textView);
        initListView();
        instanceLoaded = true;
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

        updateListView(VALUE_START, jsonMETA);

        systemLoad =  data.get("system") * 10;
        userLoad = data.get("user") * 10;
        waitLoad = data.get("wait") * 10;
        Log.d("system", systemLoad + "");
        Log.d("user", userLoad + "");
        Log.d("wait", waitLoad + "");

        systemTextView.setText("System " + PrecisionFormat.newInstance().shrink(systemLoad, 2) + "%");
        userTextView.setText("User " + PrecisionFormat.newInstance().shrink(userLoad, 2) + "%");
        waitTextView.setText("Wait " + PrecisionFormat.newInstance().shrink(waitLoad, 2) + "%");

        if (systemLoad > 60.0) {
            new StatusAlert(mContext).showNotification(Constants.CPU_NOTIF, "CPU overload " + systemLoad + "%");
        }

        int percentageInAll = (int) (systemLoad + userLoad + waitLoad);

        systemIndicatorItem.setWeight(systemLoad);
        userIndicatorItem.setWeight(userLoad);
        waitIndicatorItem.setWeight(waitLoad);

        wheelIndicatorView.setFilledPercent(percentageInAll);
        wheelIndicatorView.notifyDataSetChanged();
        wheelIndicatorView.startItemsAnimation();
    }

    private void initListView() {

        groupItem.title = "JSON META";
        items.add(groupItem);

        adapter = new ExpandableListAdapter(getActivity());
        adapter.setData(items);

        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.cpu_meta_listview);
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
        mScrollView = (ObservableScrollView) view.findViewById(R.id.cpu_ScrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    @Override
    public void onStop() {
        mTimer.cancel();
        super.onStop();
    }
}
