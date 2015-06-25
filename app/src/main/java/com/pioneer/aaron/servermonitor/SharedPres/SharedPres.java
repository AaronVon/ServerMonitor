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
    private SharedPreferences.Editor editor;

    public SharedPres(Context mContext) {
        preferences = mContext.getSharedPreferences(Constants.SHAREDPREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public boolean isFirstRun() {
        boolean isFirst;
        isFirst = preferences.getBoolean(Constants.INITIALIZED, true);

        if (isFirst) {
//            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.INITIALIZED, false);
            editor.putInt(Constants.REFRESHGAP, 10);
            editor.putBoolean(Constants.SETTINGTIP, true);
            editor.commit();
            editor.clear();
        }

        return isFirst;
    }

    public void resetPrefs() {
//        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.INITIALIZED, true);
        editor.putInt(Constants.REFRESHGAP, 10);
        editor.putBoolean(Constants.SETTINGTIP, true);
        editor.commit();
        editor.clear();
    }

    public void setRefreshGap(int value) {
        editor.putInt(Constants.REFRESHGAP, value);
        editor.commit();
        editor.clear();
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
        return preferences.getInt(key, defValue);
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
        return preferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return preferences.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
