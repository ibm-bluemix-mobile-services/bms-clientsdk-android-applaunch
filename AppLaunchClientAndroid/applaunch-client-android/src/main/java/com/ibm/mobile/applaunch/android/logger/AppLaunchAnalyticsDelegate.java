/*
 * IBM Confidential OCO Source Materials
 *
 * 5725-I43 Copyright IBM Corp. 2006, 2015
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 */
package com.ibm.mobile.applaunch.android.logger;

import com.ibm.mobilefirstplatform.clientsdk.android.analytics.internal.AnalyticsDelegate;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

public class AppLaunchAnalyticsDelegate implements AnalyticsDelegate {
    @Override
    public void enable() {
        AppLaunchAnalytics.enable();
    }

    @Override
    public void disable() {
        AppLaunchAnalytics.disable();
    }

    @Override
    public boolean isEnabled() {
        return AppLaunchAnalytics.isEnabled();
    }

    @Override
    public void send() {
        AppLaunchAnalytics.send();
    }

    @Override
    public void send(Object responseListener) {
        AppLaunchAnalytics.send((ResponseListener)responseListener);
    }

    @Override
    public void log(JSONObject eventMetadata) {
        AppLaunchAnalytics.log(eventMetadata);
    }

    @Override
    public void setUserIdentity(String username) {
        AppLaunchAnalytics.setUserIdentity(username);
    }

    @Override
    public void clearUserIdentity() {
        AppLaunchAnalytics.clearUserIdentity();
    }

    @Override
    public String getClientAPIKey() {
        return AppLaunchAnalytics.getClientApiKey();
    }

    @Override
    public String getAppName() {
        return AppLaunchAnalytics.getAppName();
    }
}
