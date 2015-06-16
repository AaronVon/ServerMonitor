package com.pioneer.aaron.servermonitor.JsonUtilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aaron on 6/16/15.
 */
public class NetworkjsonParser {

    /**
     * @return a NetworkjosnParser instance
     */
    public static NetworkjsonParser newInstance() {
        return new NetworkjsonParser();
    }

    public Map<String, Float> parseJSON(String JSONString) {
        HashMap<String, Float> map = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(JSONString);

            JSONObject objectDATA_vpns0 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns0");


            JSONObject objectDATA_vpns1 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns1");

            JSONObject objectDATA_vpns2 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns2");

            JSONObject objectDATA_vpns3 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns3");

            JSONObject objectDATA_vpns4 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns4");

            JSONObject objectDATA_vpns5 = new JSONObject(jsonArray.getJSONObject(0).toString())
                    .getJSONObject("DATA").getJSONObject("Network").getJSONObject("Interface").getJSONObject("vpns5");

            /*JSONArray vpns0_r = objectDATA_vpns0.getJSONArray("rx_bytes");
            JSONArray vpns0_w = objectDATA_vpns0.getJSONArray("tx_bytes");*/

            JSONObject vpns0_r = new JSONObject(objectDATA_vpns0.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns0_t = new JSONObject(objectDATA_vpns0.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns0_r", Float.parseFloat(vpns0_r.get("y").toString()));
            map.put("vpns0_t", Float.parseFloat(vpns0_t.get("y").toString()));

            JSONObject vpns1_r = new JSONObject(objectDATA_vpns1.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns1_t = new JSONObject(objectDATA_vpns1.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns1_r", Float.parseFloat(vpns1_r.get("y").toString()));
            map.put("vpns1_t", Float.parseFloat(vpns1_t.get("y").toString()));

            JSONObject vpns2_r = new JSONObject(objectDATA_vpns2.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns2_t = new JSONObject(objectDATA_vpns2.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns2_r", Float.parseFloat(vpns2_r.get("y").toString()));
            map.put("vpns2_t", Float.parseFloat(vpns2_t.get("y").toString()));

            JSONObject vpns3_r = new JSONObject(objectDATA_vpns3.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns3_t = new JSONObject(objectDATA_vpns3.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns3_r", Float.parseFloat(vpns3_r.get("y").toString()));
            map.put("vpns3_t", Float.parseFloat(vpns3_t.get("y").toString()));

            JSONObject vpns4_r = new JSONObject(objectDATA_vpns4.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns4_t = new JSONObject(objectDATA_vpns4.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns4_r", Float.parseFloat(vpns4_r.get("y").toString()));
            map.put("vpns4_t", Float.parseFloat(vpns4_t.get("y").toString()));

            JSONObject vpns5_r = new JSONObject(objectDATA_vpns5.getJSONArray("rx_bytes").getJSONObject(0).toString());
            JSONObject vpns5_t = new JSONObject(objectDATA_vpns5.getJSONArray("tx_bytes").getJSONObject(0).toString());
            map.put("vpns5_r", Float.parseFloat(vpns5_r.get("y").toString()));
            map.put("vpns5_t", Float.parseFloat(vpns5_t.get("y").toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
