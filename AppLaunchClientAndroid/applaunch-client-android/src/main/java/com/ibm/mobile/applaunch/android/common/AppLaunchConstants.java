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

    //broadcast registration
    public static final String ACTIONS_RECEIVED_RECEIVER = "com.applaunch.broadcast.action";
}