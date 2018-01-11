package com.ibm.mobile.applaunch.android.api;


import com.ibm.mobile.applaunch.android.AppLaunchFailResponse;
import com.ibm.mobile.applaunch.android.AppLaunchResponse;

/**
 * Created by norton on 7/25/17.
 */

public interface AppLaunchInternalListener {
    public void onSuccess(AppLaunchResponse appLaunchResponse);
    public void onFailure(AppLaunchFailResponse appLaunchFailResponse);
}