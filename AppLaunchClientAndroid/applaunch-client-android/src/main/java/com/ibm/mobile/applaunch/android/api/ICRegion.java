package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 1/2/18.
 */

public enum ICRegion {


    US_SOUTH(".us-south.containers.mybluemix.net"),

    UNITED_KINGDOM(".eu-gb.containers.mybluemix.net"),

    SYDNEY(".sydney.containers.mybluemix.net"),

    US_SOUTH_STAGING("-staging.us-south.containers.mybluemix.net"),

    UNITED_KINGDOM_STAGING("-staging.eu-gb.containers.mybluemix.net"),

    SYDNEY_STAGING("-staging.sydney.containers.mybluemix.net"),

    US_SOUTH_DEV("-dev.us-south.containers.mybluemix.net"),

    UNITED_KINGDOM_DEV("-dev.eu-gb.containers.mybluemix.net"),

    SYDNEY_DEV("-dev.sydney.containers.mybluemix.net");

    private String region;


    ICRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region;
    }
}
