package com.pioneer.aaron.servermonitor.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pioneer.aaron.servermonitor.Constants.ExpandableListView_Helper.*;
import com.pioneer.aaron.servermonitor.R;
import com.pioneer.aaron.servermonitor.Widgets.AnimatedExpandableListView;

import java.util.List;

/**
 * adapter for customized expandablelistview(anim)
 * */
public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private LayoutInflater inflater;
    private List<GroupItem> items;

    public ExpandableListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        ChildItem childItem = getChild(groupPosition, childPosition);
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            childHolder.title = (TextView) convertView.findViewById(R.id.textTitle);
            childHolder.hint = (TextView) convertView.findViewById(R.id.textHint);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        childHolder.title.setText(childItem.title);
        childHolder.hint.setText(childItem.hint);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        GroupItem groupItem = getGroup(groupPosition);

        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.group_item, parent, false);

            groupHolder.title = (TextView) convertView.findViewById(R.id.textTitle);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.title.setText(groupItem.title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
