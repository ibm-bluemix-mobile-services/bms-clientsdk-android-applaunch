/*
 *     Copyright 2018 IBM Corp.
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.ibm.mobile.applaunch.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.ibm.mobile.applaunch.android.AppLaunchFailResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by norton on 12/18/17.
 */

public class AppLaunchCacheManager {

    private static AppLaunchCacheManager thisInstance = null;
    private static String EXTENSION = ".json";
    private static String SERVICE_NAME = "applaunch";
    private static final String APP_LAUNCH = "AppLaunch";
    private SharedPreferences sharedpreferences =null;
    private Context context;


    private AppLaunchCacheManager() {
        super();

    }

    public synchronized static AppLaunchCacheManager getInstance() {
        if (thisInstance == null) {
            thisInstance = new AppLaunchCacheManager();
        }
        return thisInstance;
    }

    protected void initializeCache(Context context){
        this.context= context;
        sharedpreferences = context.getSharedPreferences(APP_LAUNCH, Context.MODE_PRIVATE);
    }

    protected void destroyCache(){
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.commit();
    }

    protected String getString(String key, String defaultValue){
        if(key!=null){
          return  sharedpreferences.getString(key,defaultValue);
        }
        return defaultValue;
    }

    protected boolean getBoolean(String key, boolean defaultValue){
        if(key!=null){
            return  sharedpreferences.getBoolean(key,defaultValue);
        }
        return defaultValue;
    }


    protected long getLong(String key, long defaultValue){
        if(key!=null){
            return  sharedpreferences.getLong(key,defaultValue);
        }
        return defaultValue;
    }

    /**
     * Add the string value to the local cache with the given key
     * @param key
     * @param value
     */
    protected void addString(String key, String value){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key,value);
        editor.commit();
    }


    /**
     * Add the string value to the local cache with the given key
     * @param key
     * @param value
     */
    protected void addLong(String key, long value){
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key,value);
        editor.commit();
    }

    /**
     * Add the boolean value to the local cache with the given key
     * @param key
     * @param value
     */
    protected void addBoolean(String key, boolean value){
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key,value);
        editor.commit();
    }


    private SharedPreferences.Editor getEditor(){
        return sharedpreferences.edit();
    }

    protected void loadDefaultFeatures(AppLaunchListener appLaunchListener)  {
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list("");
            for (String file : files) {
                Log.d("AppLaunchCacheManager", file);
                if (file.toLowerCase().startsWith(SERVICE_NAME) && file.endsWith(EXTENSION)) {
                    JSONObject featureJson = getFeatureFromFile(assetManager.open(file));
                    addFeatureToCache(featureJson);
                }
            }
        }catch (Exception ex){
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.DEFAULT_FEATURE_LOAD_FAILURE,"Failed to load error messages");
            appLaunchListener.onFailure(appLaunchFailResponse);
        }
    }

    protected void addFeatureToCache(JSONObject jsonObject) throws JSONException{
        addString(jsonObject.getString("code"),jsonObject.toString());
    }


    private JSONObject getFeatureFromFile(InputStream inputStream) throws JSONException, IOException {
        BufferedReader reader = null;
        StringBuilder featureFile = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            // do reading, usually loop until end of file reading
            String line;
            while ((line = reader.readLine()) != null) {
                featureFile.append(line);
            }

        }  finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return validateFeatureContent(featureFile.toString());
    }

    /**
     *
     * @param jsonContent
     * @return
     * @throws JSONException
     */
    private JSONObject validateFeatureContent(String jsonContent) throws JSONException {
        JSONObject featureObject = new JSONObject(jsonContent);
        if(featureObject.has("name") && featureObject.has("code")){
            return  featureObject;
        }
        throw new JSONException("Invalid Json File "+jsonContent);
    }



}
