package com.ibm.mobile.applaunch.android.internal;

import com.ibm.mobile.applaunch.android.api.ICRegion;

/**
 * Created by norton on 1/2/18.
 */

public class AppLaunchUrlBuilder {

    private static String baseURL;
    private static String applicationID;
    private static String deviceID;
    private static String userId;
    private static String FORWARDSLASH = "/";
    private static String APPS = "/apps";
    private static String DEVICES = "/devices";
    private static String ACTIONS = "/actions";
    private static String EVENTS = "/events";
    private static String METRICS = "/metrics";
    private static String USERS = "/users";
    private static String MOBILESERVICES = "https://applaunch";
    private static String APPLAUNCH_CONTEXT =  "/applaunch/v1";
    private static String MOBILESERVICES_DEV="https://mobileservices-dev.us-south.containers.mybluemix.net";


    public AppLaunchUrlBuilder(ICRegion region, String appID, String deviceID,String userId) {
        if(region.equals(ICRegion.US_SOUTH_DEV)){
            this.baseURL = MOBILESERVICES_DEV+APPLAUNCH_CONTEXT;
        }else{
            this.baseURL = MOBILESERVICES + region.toString() + APPLAUNCH_CONTEXT;
        }
        this.applicationID = appID;
        this.deviceID = deviceID;
        this.userId = userId;
    }

    public String getBaseURL() {
        return this.baseURL + APPS + FORWARDSLASH + applicationID ;
    }


    public String getAppRegistrationURL() {
        return getBaseURL() + DEVICES;
    }

    public String getUserURL() {
        return getAppRegistrationURL() + FORWARDSLASH + deviceID;
    }

    public String getActionURL() {
        return getAppRegistrationURL() + FORWARDSLASH + deviceID + ACTIONS;
    }


    public String getUpdateURL() {
        return getAppRegistrationURL() + FORWARDSLASH + deviceID;
    }


    public String getMetricsURL() {
        return getAppRegistrationURL() + FORWARDSLASH + deviceID + EVENTS + METRICS;
    }

    public String getAnalyzerURL() {
        return getBaseURL() + USERS + userId;
    }

}
