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
package com.engage.logger;

import com.ibm.mobilefirstplatform.clientsdk.android.analytics.internal.AnalyticsDelegate;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

public class EngageAnalyticsDelegate implements AnalyticsDelegate {
    @Override
    public void enable() {
        EngageAnalytics.enable();
    }

    @Override
    public void disable() {
        EngageAnalytics.disable();
    }

    @Override
    public boolean isEnabled() {
        return EngageAnalytics.isEnabled();
    }

    @Override
    public void send() {
        EngageAnalytics.send();
    }

    @Override
    public void send(Object responseListener) {
        EngageAnalytics.send((ResponseListener)responseListener);
    }

    @Override
    public void log(JSONObject eventMetadata) {
        EngageAnalytics.log(eventMetadata);
    }

    @Override
    public void setUserIdentity(String username) {
        EngageAnalytics.setUserIdentity(username);
    }

    @Override
    public void clearUserIdentity() {
        EngageAnalytics.clearUserIdentity();
    }

    @Override
    public String getClientAPIKey() {
        return EngageAnalytics.getClientApiKey();
    }

    @Override
    public String getAppName() {
        return EngageAnalytics.getAppName();
    }
}
