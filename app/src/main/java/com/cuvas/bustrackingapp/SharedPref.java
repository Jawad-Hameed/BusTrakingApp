package com.cuvas.bustrackingapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("my_shared_pref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        String value = sharedPreferences.getString(key, "");
        return  value;
    }
}
