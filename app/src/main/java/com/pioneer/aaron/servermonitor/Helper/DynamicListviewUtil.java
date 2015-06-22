package com.pioneer.aaron.servermonitor.Helper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.pioneer.aaron.servermonitor.Adapters.ExpandableListAdapter;
import com.pioneer.aaron.servermonitor.Widgets.AnimatedExpandableListView;

/**
 * dynamicly set lisview height which is in scrollview
 * Created by Aaron on 6/22/15.
 */
public class DynamicListviewUtil {

    public static DynamicListviewUtil newInstance() {
        return new DynamicListviewUtil();
    }

    /**
     * set ExpandableListView height dynamicly
     */
    public void setExpandableListViewHeight(ExpandableListView listView, int groupPosition) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
//        Log.d("listview", listAdapter.getGroupCount() + "\n" + listAdapter.getRealChildrenCount(0));
        for (int i = 0; i < listAdapter.getGroupCount(); ++i) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != groupPosition)) ||
                    ((!listView.isGroupExpanded(i)) && (i == groupPosition))) {
                for (int j = 0; j < listAdapter.getRealChildrenCount(i); ++j) {
                    View childItem = listAdapter.getRealChildView(i, j, false, null, listView);
                    childItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += childItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10) {
            height = 200;
        }

        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * reset height
     */

    public void resetHeight(ExpandableListView listView, int groupPosition) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); ++i) {
            View groupItem = listAdapter.getGroupView(groupPosition, false, null, listView);
            totalHeight += groupItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10) {
            height = 200;
        }
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
