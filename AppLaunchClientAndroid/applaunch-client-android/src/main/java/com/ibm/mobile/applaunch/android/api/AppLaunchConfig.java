package com.ibm.mobile.applaunch.android.api;

import android.app.Application;
import android.content.Context;

/**
 * Created by norton on 7/25/17.
 */

public class AppLaunchConfig {

    private Application application;
    private String bluemixRegion;
    private String applicationId;
    private String clientSecret;
    private Context context;
    private String userID;
    private String customerType;


    /**
     * @param application
     * @param bluemixRegion
     * @param appId
     * @param clientSecret
     */
    protected AppLaunchConfig(Application application, String bluemixRegion, String appId, String clientSecret) {
        this.application = application;
        this.context = application.getApplicationContext();
        this.bluemixRegion = bluemixRegion;
        this.applicationId = appId;
        this.clientSecret = clientSecret;
    }

    protected Context getContext() {
        return context;
    }

    protected String getBluemixRegion() {
        return bluemixRegion;
    }

    protected String getApplicationId() {
        return applicationId;
    }

    protected String getClientSecret() {
        return clientSecret;
    }


    protected String getUserID() { return userID; }


    protected Application getApplication() {
        return application;
    }

    protected String getCustomerType() {
        return customerType;
    }

    protected void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    protected void setUserID(String userID) {
        this.userID = userID;
    }
}
