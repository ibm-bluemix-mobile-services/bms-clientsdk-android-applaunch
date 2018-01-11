package com.ibm.mobile.applaunch.android.api;

import android.app.Application;

import com.ibm.mobile.applaunch.android.common.AppLaunchUtils;

/**
 * Created by norton on 7/25/17.
 */

public class AppLaunchConfig {

   // private Application application;
    private String bluemixRegion;
    private String applicationId;
    private String clientSecret;
    // private Context context;
    private String userID;
    private String customerType;
    private RefreshPolicy refreshPolicy;
    private long cacheExpiration;
    private long eventFlushInterval;
    private String deviceId;


    /**
     * @param application
     * @param bluemixRegion
     * @param appId
     * @param clientSecret
     */
    protected AppLaunchConfig(Application application, String bluemixRegion, String appId, String clientSecret) {
      //  this.application = application;
        //   this.context = application.getApplicationContext();
        this.bluemixRegion = bluemixRegion;
        this.applicationId = appId;
        this.clientSecret = clientSecret;
    }


    private AppLaunchConfig(Builder builder) {
        this.refreshPolicy = builder.refreshPolicy;
        this.eventFlushInterval = builder.eventFlushInterval;
        this.cacheExpiration = builder.cacheExpiration;
        this.deviceId = builder.deviceId;
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


    protected String getUserID() {
        return userID;
    }


//    protected Application getApplication() {
//        return application;
//    }

    protected String getCustomerType() {
        return customerType;
    }

    protected void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    protected void setUserID(String userID) {
        this.userID = userID;
    }

 //   protected void setApplication(Application application) {
   //     this.application = application;
   // }

    protected void setBluemixRegion(String bluemixRegion) {
        this.bluemixRegion = bluemixRegion;
    }

    protected void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    protected void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getDeviceId() {
        if(deviceId==null){
            deviceId  = AppLaunchUtils.getDeviceId();
        }
        return deviceId;
    }

    public RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    public long getCacheExpiration() {
        return cacheExpiration;
    }

    public float getEventFlushInterval() {
        return eventFlushInterval;
    }

    public static class Builder {

        private RefreshPolicy refreshPolicy = RefreshPolicy.REFRESH_ON_EVERY_START;
        private long cacheExpiration=60;
        private long eventFlushInterval=60;
        private String deviceId;

        public Builder() {
        }


        public Builder loadFeatureDefaults(String label) {

            return this;
        }

        public Builder fetchPolicy(RefreshPolicy refreshPolicy) {
            this.refreshPolicy = refreshPolicy;
            return this;
        }

        public Builder cacheExpiration(long minutes) {
            this.cacheExpiration = minutes*60;
            return this;
        }

        public Builder eventFlushInterval(long minutes) {
            this.eventFlushInterval = minutes*60;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public AppLaunchConfig build() {
            return new AppLaunchConfig(this);
        }
    }

}
