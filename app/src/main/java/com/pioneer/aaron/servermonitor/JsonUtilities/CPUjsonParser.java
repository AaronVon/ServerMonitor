package com.pioneer.aaron.servermonitor.JsonUtilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 6/14/15.
 */
public class CPUjsonParser {
    public CPUjsonParser() {
    }

    public static CPUjsonParser newInstance() {
        return new CPUjsonParser();
    }

    public Map<String, Float> parseJSON(String JSONString) {
        HashMap<String, Float> map = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(JSONString);
            JSONObject objectDATA = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("CPU").getJSONObject("cpu0");

            JSONArray userArray = objectDATA.getJSONArray("user");
            JSONObject userObject = new JSONObject(userArray.getJSONObject(0).toString());

            JSONArray systemArray = objectDATA.getJSONArray("system");
            JSONObject systemObject = new JSONObject(systemArray.getJSONObject(0).toString());

            JSONArray waitArray = objectDATA.getJSONArray("wait");
            JSONObject waitObject = new JSONObject(waitArray.getJSONObject(0).toString());

            map.put("user", Float.parseFloat(userObject.get("y").toString()));
            map.put("system", Float.parseFloat(systemObject.get("y").toString()));
            map.put("wait", Float.parseFloat(waitObject.get("y").toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
