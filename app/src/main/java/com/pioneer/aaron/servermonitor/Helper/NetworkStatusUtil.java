package com.pioneer.aaron.servermonitor.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Aaron on 6/16/15.
 */
public class NetworkStatusUtil {
    private Context context;

    public NetworkStatusUtil(Context context) {
        this.context = context;
    }

    public boolean isAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (null != info) {
            return info.isAvailable();
        } else {
            return false;
        }
    }
}
