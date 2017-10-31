package com.applaunch.api;

import java.util.Hashtable;

/**
 * Created by norton on 9/23/17.
 */

public class AppLaunchParameters {

    private Hashtable paramters;

    public AppLaunchParameters() {
        paramters = new Hashtable<>();
     }

    public void put(String key,String value){
        paramters.put(key,value);
    }

    public void put(String key,boolean value){
        paramters.put(key,String.valueOf(value));
    }


    public void put(String key,int value){
        paramters.put(key,String.valueOf(value));
    }

    protected Hashtable getParameters(){
        return paramters;
    }

}
