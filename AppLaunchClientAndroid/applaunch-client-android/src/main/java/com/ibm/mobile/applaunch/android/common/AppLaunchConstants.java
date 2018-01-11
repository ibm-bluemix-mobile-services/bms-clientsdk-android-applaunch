package com.ibm.mobile.applaunch.android.common;


/**
 * Created by norton on 9/22/17.
 */

public class AppLaunchConstants {

    public static final String VERSION = "version";
    public static final String APP_LAUNCH_CLIENT_PROPS_NAME = "applaunchclient.properties";
    public static final String APP_LAUNCH_SERVER_HOST = "engageServerHost";
    public static final String APP_LAUNCH_SERVER_PROTOCOL = "engageServerProtocol";
    public static final String APP_LAUNCH_SERVER_PORT = "engageServerPort";
    public static final String APP_LAUNCH_SERVER_CONTEXT = "engageServerContext";
    public static final String ANALYZER_SERVER_HOST = "analyzerServerHost";
    public static final String ANALYZER_SERVER_PROTOCOL = "analyzerServerprotocol";
    public static final String ANALYZER_SERVER_PORT = "analyzerServerport";
    public static final String ANALYZER_SERVER_CONTEXT = "analyzerServerContext";

    //URL
    public static final String REGION_US_SOUTH_STAGING="mobileservices-staging.us-south.containers.mybluemix.net";
    public static final String REGION_US_SOUTH_DEV="mobileservices-dev.us-south.containers.mybluemix.net";
    public static final String REGION_US_SOUTH="mobileservices.us-south.containers.mybluemix.net";

    //sharedpref keys
    //public static final String FIRST_TIME_USER = "isFirstTime";
    public static final String REG_RESPONSE = "regResponse";
    public static final String ACTIONS_INVOKED="actionsInvoked";
    public static final String ACTIONS="actions";
    public static final String ACTIONS_LAST_REFRESH="actionsLastRefresh";
    public static final String ANALYZER_URL="analyzerUrl";
    public static final String APP_USER="appUser";
    public static final String INAPP_MESSAGES="inappmessages";


    //inapp messages contants
    public static final String TRIGGER_FIRST_LAUNCH = "OnFirstAppLaunch";
    public static final String TRIGGER_EVERY_LAUNCH = "OnEveryAppLaunch";
    public static final String TRIGGER_EVERY_ALTERNATE_LAUNCH = "OnEveryAlternateAppLaunch";
    public static final String TRIGGER_ONCE_AND_ONLY_ONCE = "OnceAndOnlyOnce";

    //json keys
    public static final String PROPERTIES = "properties";
    public static final String CODE = "code";
    public static final String VALUES = "value";
}