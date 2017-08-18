package com.engage;

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import org.json.JSONObject;

/**
 * Created by norton on 7/26/17.
 */

public class EngageResponse {

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + EngageFailResponse.class.getSimpleName());

    private int status;
    private String responseText;
    protected String statusText;
    private JSONObject responseJSON;


    public EngageResponse() {
        super();
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public JSONObject getResponseJSON() {
        return responseJSON;
    }

    public void setResponseJSON(JSONObject responseJSON) {
        this.responseJSON = responseJSON;
    }
}
