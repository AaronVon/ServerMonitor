package com.pioneer.aaron.servermonitor.JsonUtilities;

import android.util.Log;

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

            JSONObject xvdaReadObject = new JSONObject(objectDATA_1.getJSONArray("reads").getJSONObject(0).toString());
            JSONObject xvdaWriteObject = new JSONObject(objectDATA_1.getJSONArray("writes").getJSONObject(0).toString());

            JSONObject xvdbReadObject = new JSONObject(objectDATA_2.getJSONArray("reads").getJSONObject(0).toString());
            JSONObject xvdbWriteObject = new JSONObject(objectDATA_2.getJSONArray("writes").getJSONObject(0).toString());

            map.put("xvdar", Float.parseFloat(xvdaReadObject.get("y").toString()));
            map.put("xvdaw", Float.parseFloat(xvdaWriteObject.get("y").toString()));
            map.put("xvdbr", Float.parseFloat(xvdbReadObject.get("y").toString()));
            map.put("xvdbw", Float.parseFloat(xvdbWriteObject.get("y").toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
