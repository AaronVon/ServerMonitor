package com.pioneer.aaron.servermonitor.Constants;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 6/20/15.
 */
public interface ExpandableListView_Helper {
    class GroupItem {
        public String title;
        public List<ChildItem> items = new ArrayList<ChildItem>();
    }

    class ChildItem {
        public String title;
        public  String hint;
    }
    class ChildHolder {
        public TextView title;
        public TextView hint;
    }

    class GroupHolder {
        public TextView title;
    }
}
