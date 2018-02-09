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

import android.app.Application;

import com.ibm.mobile.applaunch.android.common.AppLaunchUtils;

/**
 * Created by norton on 7/25/17.
 */

/**
 * AppLaunchConfig contains configuration of AppLaunch Service which is used by AppLaunch APIs.
 * This method will initialize the AppLaunchConfig with the help of Builder Class. The builder class parameters are optional.
 */
public class AppLaunchConfig {

    private String bluemixRegion;
    private String applicationId;
    private String clientSecret;
    private String userID;
    private RefreshPolicy refreshPolicy;
    private long cacheExpiration;
    private long eventFlushInterval;
    private String deviceId;

    protected AppLaunchConfig(Application application, String bluemixRegion, String appId, String clientSecret) {
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

    protected void setUserID(String userID) {
        this.userID = userID;
    }

    protected void setBluemixRegion(String bluemixRegion) {
        this.bluemixRegion = bluemixRegion;
    }

    protected void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    protected void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    protected String getDeviceId() {
        if(deviceId==null){
            deviceId  = AppLaunchUtils.getDeviceId();
        }
        return deviceId;
    }

    protected RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    protected long getCacheExpiration() {
        return cacheExpiration;
    }

    protected float getEventFlushInterval() {
        return eventFlushInterval;
    }

    /**
     * Builder class of AppLaunchConfig.
     */
    public static class Builder {

        private RefreshPolicy refreshPolicy = RefreshPolicy.REFRESH_ON_EVERY_START;
        private long cacheExpiration=60;
        private long eventFlushInterval=60;
        private String deviceId;

        /**
         * Initializer for builder class of AppLanchConfig.
         */
        public Builder() {
        }

        /**
         * This method can be used to set RefreshPolicy decides on how frequently the engagements should be fetched from the server. If not set, The default value will be RefreshPolicy.REFRESH_ON_EVERY_START.
         *
         * @param refreshPolicy Refresh Policy
         * @return
         */
        public Builder fetchPolicy(RefreshPolicy refreshPolicy) {
            this.refreshPolicy = refreshPolicy;
            return this;
        }

        /**
         * This method can be used to set cacheExpiration time which decides the time interval of the engagements should be valid for. On expiration time the actions are fetched from the server. This parameter has effect when the RefreshPolicy is set to RefreshPolicy.REFRESH_ON_EXPIRY or RefreshPolicy.BACKGROUND_REFRESH. If not set, The default value will be 30 minutes.
         *
         * @param minutes Cache Expiration time
         * @return
         */
        public Builder cacheExpiration(long minutes) {
            this.cacheExpiration = minutes*60;
            return this;
        }

        /**
         * This method can be used to set eventFlushInterval time which decides the time interval of the events which should be sent to the server. If not set, The default value will be 30 minutes.
         *
         * @param minutes Flush interval Time
         * @return
         */
        public Builder eventFlushInterval(long minutes) {
            this.eventFlushInterval = minutes*60;
            return this;
        }

        /**
         * This method can be used to set deviceID value which is used to override device ID. This parameter must be unique. If not specified, default deviceID generation mechanism is used by SDK.
         *
         * @param deviceId Device ID value
         * @return
         */
        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        /**
         * This method builds AppLaunch Configuration object.
         *
         * @return AppLaunchConfig instance
         */
        public AppLaunchConfig build() {
            return new AppLaunchConfig(this);
        }
    }

}
