package com.pioneer.aaron.servermonitor.Fragments;

import android.content.Context;
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
import com.pioneer.aaron.servermonitor.Adapters.ExpandableListAdapter;
import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.Constants.ExpandableListView_Helper;
import com.pioneer.aaron.servermonitor.Helper.DynamicListviewUtil;
import com.pioneer.aaron.servermonitor.Helper.SystemTime;
import com.pioneer.aaron.servermonitor.JsonUtilities.DiskjsonParser;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.R;
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
import com.pioneer.aaron.servermonitor.JsonUtilities.NetworkjsonParser;
import com.pioneer.aaron.servermonitor.R;

import android.os.Bundle;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.pioneer.aaron.servermonitor.SharedPres.SharedPres;
import com.pioneer.aaron.servermonitor.Widgets.AnimatedExpandableListView;

import android.os.Build;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aaron on 6/12/15.
 */
public class DiskFragment extends Fragment {

    private ObservableScrollView mScrollView;
    private View rootView;
    private Timer mTimer;

    private static String VALUE_START = "";
    private static String VALUE_END = "";
    private boolean instanceLoaded = false;
    private Context mContext;
    //expandablelistview with animation
    private AnimatedExpandableListView listView;
    private ExpandableListAdapter adapter;
    private List<ExpandableListView_Helper.GroupItem> items = new ArrayList<>();
    private ExpandableListView_Helper.GroupItem groupItem = new ExpandableListView_Helper.GroupItem();

    public static DiskFragment newInstance() {
        return new DiskFragment();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUI();
            }
        }
    };

    //horbar
    private String[] data_keys = {"xvdar", "xvdaw", "xvdbr", "xvdbw"};
    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();



    /**
     * Order
     */
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;


    /**
     * Ease
     */
    private static BaseEasingMethod mCurrEasing;
    private static BaseEasingMethod mOldEasing;


    /**
     * Enter
     */
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static float mOldStartX;
    private static float mOldStartY;


    /**
     * Alpha
     */
    private static int mCurrAlpha;
    private static int mOldAlpha;



    /**
     * HorizontalBar
     */
    private static int HOR_BAR_MAX = 100;
    private final static int HOR_BAR_MIN = 0;
    private final static String[] horBarLabels = {"DiskA r", "DiskA w", "DiskB r", "DiskB w"};
    private final static float [][] horBarValues = { {6f, 7f, 2f, 4f},
            {7f, 4f, 3f, 1f} };
    private static HorizontalBarChartView mHorBarChart;
    private Paint mHorBarGridPaint;
    private TextView mHorBarTooltip;

    private final OnEntryClickListener horBarEntryListener = new OnEntryClickListener(){
        @Override
        public void onClick(int setIndex, int entryIndex, Rect rect) {
            if(mHorBarTooltip == null)
                showHorBarTooltip(setIndex, entryIndex, rect);
            else
                dismissHorBarTooltip(setIndex, entryIndex, rect);
        }
    };

    private final OnClickListener horBarClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            if(mHorBarTooltip != null)
                dismissHorBarTooltip(-1, -1, null);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.diskio_layout,
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.diskio_ScrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
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

    public void init() {

        mCurrOverlapFactor = 1;
        mCurrEasing = new QuintEaseOut();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        mOldOverlapFactor = 1;
        mOldEasing = new QuintEaseOut();
        mOldStartX = -1;
        mOldStartY = 0;
        mOldAlpha = -1;
        mHorBarChart = (HorizontalBarChartView) rootView.findViewById(R.id.horbarchart_disk);
//        mHorBarChart.setOnEntryClickListener(horBarEntryListener);
//        mHorBarChart.setOnClickListener(horBarClickListener);

        mHorBarGridPaint = new Paint();
        mHorBarGridPaint.setColor(this.getResources().getColor(R.color.bar_grid));
        mHorBarGridPaint.setStyle(Paint.Style.STROKE);
        mHorBarGridPaint.setAntiAlias(true);
        mHorBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        updateHorBarChart();
        mTimer = new Timer();
        initListView();
        instanceLoaded = true;
    }

    private void initListView() {
        groupItem.title = "JSON META";
        items.add(groupItem);

        adapter = new ExpandableListAdapter(mContext);
        adapter.setData(items);

        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.disk_meta_listview);
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

    public void updateUI() {
        Map<String, String> params = new HashMap<>();
        try {
            params.put(Constants.KEY_API, Constants.VALUE_API);
            VALUE_START = SystemTime.newInstance().getStart();
            VALUE_END = SystemTime.newInstance().getEnd();
            params.put(Constants.KEY_START, VALUE_START);
            params.put(Constants.KEY_END, VALUE_END);
            params.put(Constants.KEY_ACTION, Constants.VALUE_ACTION);
            params.put(Constants.KEY_KEYS, Constants.DISK_VALUE_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonMETA = new JsonHttpUtil().getJsonMETA(Constants.POST_URL, params);

        updateListView(VALUE_START, jsonMETA);

        Map<String, Float> data = new HashMap<>();
        data = DiskjsonParser.newInstance().parseJSON(jsonMETA);

        if (data.get("xvdar") == null || data == null) {
            return;
        }

        fillBarValues(data);
        updateHorBarChart();
    }

    private void updateListView(String hint, String title) {
        ExpandableListView_Helper.ChildItem childItem = new ExpandableListView_Helper.ChildItem();
        childItem.title = title;
        childItem.hint = hint;

        groupItem.items.add(childItem);
        adapter.notifyDataSetChanged();
    }

    public void fillBarValues(Map<String, Float> data) {

        for (int i = 0; i < data_keys.length; ++i) {
            horBarValues[0][i] = data.get(data_keys[i]);
        }
    }

    private void updateHorBarChart(){

        mHorBarChart.reset();

        BarSet barSet = new BarSet();
        Bar bar;
        for(int i = 0; i < horBarLabels.length; i++){
            bar = new Bar(horBarLabels[i], horBarValues[0][i]);
            bar.setColor(this.getResources().getColor(R.color.horbar_fill));
            barSet.addBar(bar);
        }
        mHorBarChart.addData(barSet);
        mHorBarChart.setBarSpacing(Tools.fromDpToPx(3));

        mHorBarChart.setBorderSpacing(0)
                .setAxisBorderValues(HOR_BAR_MIN, HOR_BAR_MAX, 2)
                .setGrid(HorizontalBarChartView.GridType.VERTICAL, mHorBarGridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.NONE)
                .show(getAnimation(true))
        ;
    }


    @SuppressLint("NewApi")
    private void showHorBarTooltip(int setIndex, int entryIndex, Rect rect){

        mHorBarTooltip = (TextView) getActivity().getLayoutInflater().inflate(R.layout.horbar_tooltip, null);
        mHorBarTooltip.setText(Integer.toString((int) horBarValues[setIndex][entryIndex]) + "KB");
        mHorBarTooltip.setIncludeFontPadding(false);

        LayoutParams layoutParams = new LayoutParams((int) Tools.fromDpToPx(15), (int) Tools.fromDpToPx(15));
        layoutParams.leftMargin = rect.right;
        layoutParams.topMargin = rect.top - (int) (Tools.fromDpToPx(15)/2 - (rect.bottom - rect.top)/2);
        mHorBarTooltip.setLayoutParams(layoutParams);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
            mHorBarTooltip.setAlpha(0);
            mHorBarTooltip.animate()
                    .setDuration(200)
                    .alpha(1)
                    .translationX(10)
                    .setInterpolator(enterInterpolator);
        }

        mHorBarChart.showTooltip(mHorBarTooltip);
    }


    @SuppressLint("NewApi")
    private void dismissHorBarTooltip(final int setIndex, final int entryIndex, final Rect rect){

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mHorBarTooltip.animate()
                    .setDuration(100)
                    .alpha(0)
                    .translationX(-10)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable(){
                @Override
                public void run() {
                    mHorBarChart.removeView(mHorBarTooltip);
                    mHorBarTooltip = null;
                    if(entryIndex != -1)
                        showHorBarTooltip(setIndex, entryIndex, rect);
                }
            });
        }else{
            mHorBarChart.dismissTooltip(mHorBarTooltip);
            mHorBarTooltip = null;
            if(entryIndex != -1)
                showHorBarTooltip(setIndex, entryIndex, rect);
        }
    }


    private void updateValues(HorizontalBarChartView chartView){

        chartView.updateValues(0, horBarValues[1]);
        chartView.notifyDataUpdate();
    }



	/*------------------------------------*
	 *               GETTERS              *
	 *------------------------------------*/

    private Animation getAnimation(boolean newAnim){
        if(newAnim)
            return new Animation()
                    .setAlpha(mCurrAlpha)
                    .setEasing(mCurrEasing)
                    .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                    .setStartPoint(mCurrStartX, mCurrStartY);
        else
            return new Animation()
                    .setAlpha(mOldAlpha)
                    .setEasing(mOldEasing)
                    .setOverlap(mOldOverlapFactor, mOldOverlapOrder)
                    .setStartPoint(mOldStartX, mOldStartY);
    }
}
