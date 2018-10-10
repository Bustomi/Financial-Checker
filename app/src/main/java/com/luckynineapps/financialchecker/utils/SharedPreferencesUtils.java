package com.luckynineapps.financialchecker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private static SharedPreferencesUtils instance = null;
    private SharedPreferences sharedPreferences;
    private String iv;

    public static SharedPreferencesUtils getInstance(Context ctx, String pName) {
        if (instance == null) {
            instance = new SharedPreferencesUtils(ctx.getApplicationContext(), pName);
        }
        return instance;
    }

    public SharedPreferencesUtils(Context context, String preferencesName) {
        sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public void storeData(String key, String data) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(key, data);

            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkIfDataExists(String key) {
        return sharedPreferences.contains(key);
    }

    public String getPreferenceData(String key) throws Exception {
        String data = sharedPreferences.getString(key, null);
        return data;
    }

    public void removeData(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (checkIfDataExists(key)) {
            editor.remove(key);

            editor.apply();
        }
    }

    public void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }

}