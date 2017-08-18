package com.engage.api;

import android.app.Application;
import android.content.Context;

/**
 * Created by norton on 7/25/17.
 */

public class EngageConfig {

    private Application application;
    private String bluemixRegion;
    private String applicationId;
    private String clientSecret;
    private Context context;
    private String userID;


    /**
     * @param application
     * @param bluemixRegion
     * @param appId
     * @param clientSecret
     */
    public EngageConfig(Application application, String bluemixRegion, String appId, String clientSecret,
                        String userId) {
        this.application = application;
        this.context = application.getApplicationContext();
        this.bluemixRegion = bluemixRegion;
        this.applicationId = appId;
        this.clientSecret = clientSecret;
        this.userID = userId;
    }

    public Context getContext() {
        return context;
    }

    public String getBluemixRegion() {
        return bluemixRegion;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getClientSecret() {
        return clientSecret;
    }


    public String getUserID() { return userID; }


    public Application getApplication() {
        return application;
    }

}
