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
