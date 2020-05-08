package com.example.go4lunch.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {

        public static String getString(Context context, String key){
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key,null);
        }

        public static void putString(Context context, String key, String value){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
        }

        public static int getInt(Context context, String key, int defaultvalue){
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultvalue);
        }

        public static void putInt(Context context, String key, int value){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
        }

        public static boolean getBoolean(Context context, String key){
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
        }

        public static void putBoolean(Context context, String key, boolean value){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
        }

}

