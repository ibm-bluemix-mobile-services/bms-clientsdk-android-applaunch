package com.ibm.mobile.applaunch.android;



/**
 * Created by norton on 7/25/17.
 */

public interface AppLaunchResponseListener {
    public void onSuccess(AppLaunchResponse appLaunchResponse);
    public void onFailure(AppLaunchFailResponse appLaunchFailResponse);
}
