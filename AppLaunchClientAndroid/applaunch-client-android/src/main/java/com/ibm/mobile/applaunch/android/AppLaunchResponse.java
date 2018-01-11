package com.ibm.mobile.applaunch.android;

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import org.json.JSONObject;

/**
 * Created by norton on 7/26/17.
 */

public class AppLaunchResponse {

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunchFailResponse.class.getSimpleName());

    private JSONObject responseJSON;


    public AppLaunchResponse() {
        super();
    }

    public JSONObject getResponseJSON() {
        return responseJSON;
    }

    public void setResponseJSON(JSONObject responseJSON) {
        this.responseJSON = responseJSON;
    }
}
