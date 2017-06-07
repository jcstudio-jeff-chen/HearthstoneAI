package com.jcstudio.hearthstoneai;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by jeffrey on 2016/10/7.
 *
 * 裝置儲存套件
 */

public class LocalStorage {

    private SharedPreferences sp; // Always not null because of constructor

    public LocalStorage(Context context){
        sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void clear(){
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public void remove(String key){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Nullable
    public String getString(String key){
        return sp.getString(key, null);
    }

    public void saveInt(String key, int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key){
        return sp.getInt(key, 0);
    }

    public void saveFloatArray(String key, float[] f){
        SharedPreferences.Editor editor = sp.edit();
        for(int i = 0; i < f.length; i++){
            editor.putFloat(key + "_" + i, f[i]);
        }
        editor.putInt(key + "_size", f.length);
        editor.apply();
    }

    public float[] getFloatArray(String key){
        int size = sp.getInt(key + "_size", 0);
        float[] output = new float[size];
        for(int i = 0; i < size; i++){
            output[i] = sp.getFloat(key + "_" + i, 0);
        }
        return output;
    }

    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean value){
        return sp.getBoolean(key, false);
    }
}
