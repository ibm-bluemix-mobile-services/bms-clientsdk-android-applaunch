package com.ibm.mobile.applaunch.android.api;

import com.ibm.mobile.applaunch.android.AppLaunchFailResponse;
import com.ibm.mobile.applaunch.android.AppLaunchResponse;

/**
 * Created by norton on 9/22/17.
 */

public interface AppLaunchListener {


    public void onSuccess(AppLaunchResponse response);
    public void onFailure(AppLaunchFailResponse failResponse);
}
