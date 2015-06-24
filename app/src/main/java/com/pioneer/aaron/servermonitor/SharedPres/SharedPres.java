package com.pioneer.aaron.servermonitor.SharedPres;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pioneer.aaron.servermonitor.Constants.Constants;

import java.util.Map;
import java.util.Set;

/**
 * Created by Aaron on 6/23/15.
 */
public class SharedPres implements SharedPreferences {
    private SharedPreferences preferences;

    public SharedPres(Context mContext) {
        preferences = mContext.getSharedPreferences(Constants.SHAREDPREFS, Context.MODE_PRIVATE);
    }

    public boolean isFirstRun() {
        boolean isFirst;
        isFirst = preferences.getBoolean(Constants.INITIALIZED, true);
        Log.d("isfirst", isFirst + "");

        if (isFirst) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.INITIALIZED, false);
            editor.commit();
            editor.clear();
        }

        return isFirst;
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
