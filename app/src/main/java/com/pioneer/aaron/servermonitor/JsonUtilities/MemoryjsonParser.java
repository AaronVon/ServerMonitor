package com.pioneer.aaron.servermonitor.JsonUtilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 6/15/15.
 */
public class MemoryjsonParser {
    public static MemoryjsonParser newInstance() {
        return new MemoryjsonParser();
    }

    public Map<String, Float> parseJSON(String JSONString) {
        HashMap<String, Float> map = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(JSONString);
            /*JSONObject objectDATA_1 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Memory").getJSONObject("swap");*/



            JSONObject objectDATA_2 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Memory").getJSONObject("real");

            JSONArray usedArray = objectDATA_2.getJSONArray("used");
            JSONObject usedObject = new JSONObject(usedArray.getJSONObject(0).toString());

            JSONArray freeArray = objectDATA_2.getJSONArray("free");
            JSONObject freeObject = new JSONObject(freeArray.getJSONObject(0).toString());

            map.put("used", Float.parseFloat(usedObject.get("y").toString()));
            map.put("free", Float.parseFloat(freeObject.get("y").toString()));
            Log.d("USED", usedObject.get("y").toString());
            Log.d("FREE", freeObject.get("y").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
