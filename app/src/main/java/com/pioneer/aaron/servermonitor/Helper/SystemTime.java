package com.pioneer.aaron.servermonitor.Helper;

/**
 * Created by Aaron on 6/14/15.
 */
public class SystemTime {
    public static SystemTime newInstance() {
        return new SystemTime();
    }

    public String getStart() {
        return String.valueOf(System.currentTimeMillis() / 1000 - 400);
    }

    public String getEnd() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
