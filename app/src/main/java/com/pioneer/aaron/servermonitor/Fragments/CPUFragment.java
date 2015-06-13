package com.pioneer.aaron.servermonitor.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.pioneer.aaron.servermonitor.JsonUtilities.JsonHttpUtil;
import com.pioneer.aaron.servermonitor.R;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 6/12/15.
 */
public class CPUFragment extends Fragment {

    private ObservableScrollView mScrollView;
    //params
    private static final String KEY_API = "api_key";
    private static final String VALUE_API = "F6F3C79A-A931-D5A0-789863E7C7A84825";

    private static final String KEY_START = "start";
    private static String VALUE_START = "1433643270";

    private static final String KEY_END = "end";
    private static String VALUE_END = "1433645100";

    private static final String KEY_ACTION = "api_action";
    private static final String VALUE_ACTION = "getValues";

    private static final String KEY_KEYS = "keys";
    private static final String VALUE_KEYS = "[\"CPU.*.wait\",\"CPU.*.user\",\"CPU.*.system\"]";


    public static CPUFragment newInstance() {
        return new CPUFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cpu_layout, container, false);
        //获取 JSON 并显示
        Map<String, String> params = new HashMap<>();
        try {
            params.put(KEY_API, VALUE_API);
            VALUE_START = "" + (System.currentTimeMillis() / 1000 - 3600);
            VALUE_END = "" + (System.currentTimeMillis() / 1000);
            params.put(KEY_START, VALUE_START);
            params.put(KEY_END, VALUE_END);
            params.put(KEY_ACTION, VALUE_ACTION);
            params.put(KEY_KEYS, VALUE_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String s = new JsonHttpUtil().getJsonContent("https://longview.linode.com/fetch", params);

        ((TextView) rootView.findViewById(R.id.textView)).setText(s);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.cpu_ScrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }
}
