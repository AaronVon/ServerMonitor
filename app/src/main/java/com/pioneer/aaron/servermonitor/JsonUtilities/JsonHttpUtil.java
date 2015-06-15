package com.pioneer.aaron.servermonitor.JsonUtilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Aaron on 6/13/15.
 */
public class JsonHttpUtil {

    public String getJsonMETA(String url, Map<String, String> params) {

        String JSON = "";
        try {
            JSON = new LoadAsync().execute(url, params).get().toString();
//            Map<String, Float> DATA = CPUjsonParser.newInstance().parseJSON(JSON);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return JSON;
    }

    private String getRequestData(Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                        .append("&");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//        Log.d("StringBuffer: ", stringBuffer.toString());

        return stringBuffer.toString();
    }

    private class LoadAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String url = params[0].toString();
            String RequestParams = getRequestData((Map<String, String>) params[1]);

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setUseCaches(false);
                //设置请求体的类型
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();

                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(RequestParams.toString());
                dataOutputStream.flush();
                dataOutputStream.close();

                if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String tmp = "";
                    StringBuilder JSON_Result = new StringBuilder();

                    while ((tmp = bufferedReader.readLine()) != null) {
                        tmp = new String(tmp.getBytes(), "utf-8");
                        JSON_Result.append(tmp);
                    }
                    bufferedReader.close();
                    httpURLConnection.disconnect();
//                    Log.d("JSON_Result: ", JSON_Result.toString());
                    return JSON_Result.toString();
                }
            } catch (Exception e) {
                Log.d("HTTP Exception:", e.toString());
            }
            return "";
        }
    }
}
