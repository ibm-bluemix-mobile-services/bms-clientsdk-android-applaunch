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

import com.ibm.mobile.applaunch.android.api.AppLaunchFailResponse;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import org.json.JSONObject;

/**
 * Created by norton on 7/26/17.
 */

/**
 This is the Success Response class in the AppLaunch.
 It is used to handle the success responses from the AppLaunch REST API calls.
 */
public class AppLaunchResponse {

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunchFailResponse.class.getSimpleName());

    private JSONObject responseJSON;


    public AppLaunchResponse() {
        super();
    }

    /**

     This Methode returns the response JSON for the AppLaunch calling API

     - returns: responseJSON: This returns the Engagements JSON from the AppLaunch Service
     */
    public JSONObject getResponseJSON() {
        return responseJSON;
    }

    public void setResponseJSON(JSONObject responseJSON) {
        this.responseJSON = responseJSON;
    }
}
