package com.pioneer.aaron.servermonitor.JsonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 6/15/15.
 */
public class DiskjsonParser {
    public static DiskjsonParser newInstance() {
        return new DiskjsonParser();
    }

    public Map<String, Float> parseJSON(String JSONString) {
        HashMap<String, Float> map = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(JSONString);
            JSONObject objectDATA_1 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Disk").getJSONObject("/dev/xvda");

            JSONObject objectDATA_2 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Disk").getJSONObject("/dev/xvdb");

            JSONArray xvdaReadArray = objectDATA_1.getJSONArray("reads");
            JSONArray xvdaWriteArray = objectDATA_1.getJSONArray("writes");

            JSONArray xvdbReadArray = objectDATA_2.getJSONArray("reads");
            JSONArray xvdbWriteArray = objectDATA_2.getJSONArray("writes");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
