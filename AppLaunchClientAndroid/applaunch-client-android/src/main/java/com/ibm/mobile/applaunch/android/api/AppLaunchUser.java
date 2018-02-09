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
 * Created by norton on 12/29/17.
 */


/**
 * AppLaunchUser contains user information which is used by AppLaunch APIs.
 */
public class AppLaunchUser {

    private String userId;
    private Hashtable parameters;


    private AppLaunchUser(Builder builder) {
        this.userId = builder.userId;
        this.parameters= builder.parameters;
    }

    protected String getUserId() {
        return userId;
    }

    protected Hashtable getParameters() {
        return parameters;
    }

    /**
     * Builder class of AppLaunchUser.
     */
    public static class Builder {
        private String userId;

        private Hashtable parameters = new Hashtable();

        /**
         * Initializer for builder class of AppLaunchUser.
         */
        public Builder() {
        }

        /**
         * Use this method to set userID
         * @param userId
         * @return
         */
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * This is an optional method which can be used to set any custom user attribute of String type.
         *
         * @param key       Key value
         * @param value     Value of String type
         * @return
         */
        public Builder custom(String key, String value) {
            parameters.put(key, value);
            return this;
        }

        /**
         * This is an optional method which can be used to set any custom user attribute of String type.
         *
         * @param key       Key value
         * @param value     Value of boolean type
         * @return
         */
        public Builder custom(String key, boolean value) {
            parameters.put(key, value);
            return this;
        }

        /**
         * This is an optional method which can be used to set any custom user attribute of String type.
         *
         * @param key       Key value
         * @param value     Value of integer type
         * @return
         */
        public Builder custom(String key, int value) {
            parameters.put(key, value);
            return this;
        }

        /**
         * This method builds AppLaunch User object.
         *
         * @return AppLaunchUser object.
         */
        public AppLaunchUser build() {
            return new AppLaunchUser(this);
        }
    }
}
