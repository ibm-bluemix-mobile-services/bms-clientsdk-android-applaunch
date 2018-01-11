package com.ibm.mobile.applaunch.android.api;

import java.util.Hashtable;

/**
 * Created by norton on 12/29/17.
 */

public class AppLaunchUser {

    private String userId;
    private Hashtable parameters;


    private AppLaunchUser(Builder builder) {
        this.userId = builder.userId;
        this.parameters= builder.parameters;
    }

    public String getUserId() {
        return userId;
    }

    public Hashtable getParameters() {
        return parameters;
    }

    public static class Builder {
        private String userId;

        private Hashtable parameters = new Hashtable();

        public Builder() {
        }


        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder custom(String key, String value) {
            parameters.put(key, value);
            return this;
        }

        public AppLaunchUser build() {
            return new AppLaunchUser(this);
        }
    }
}
