package com.example.androidproject.DataManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.ValueEventListener;

public class SharedPreferenceManager {
    private static SharedPreferenceManager instance;

    public static SharedPreferenceManager getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }

    public void saveSharedPreference(ValueEventListener context, String key, String userId) {
       /* SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(key, userId);
        editor.apply();*/
    }

    public String getSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
