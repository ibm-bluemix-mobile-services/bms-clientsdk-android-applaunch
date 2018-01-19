package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 1/2/18.
 */

public enum ICRegion {


    US_SOUTH(".ng.bluemix.net"),

    UNITED_KINGDOM(".eu-gb.bluemix.net"),

    SYDNEY(".au-syd.bluemix.net"),

    US_SOUTH_STAGING(".stage1.ng.bluemix.net"),

    UNITED_KINGDOM_STAGING(".stage1.eu-gb.bluemix.net"),

    SYDNEY_STAGING(".stage1.au-syd.mybluemix.net"),

    US_SOUTH_DEV(".dev.ng.mybluemix.net"),

    UNITED_KINGDOM_DEV(".dev.eu-gb.bluemix.net"),

    SYDNEY_DEV(".dev.au-syd.bluemix.net");
    

    private String region;


    ICRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region;
    }
}
