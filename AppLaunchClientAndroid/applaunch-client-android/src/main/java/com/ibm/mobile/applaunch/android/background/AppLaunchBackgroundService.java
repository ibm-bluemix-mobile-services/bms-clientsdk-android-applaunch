package com.ibm.mobile.applaunch.android.background;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by norton on 12/28/17.
 */

public class AppLaunchBackgroundService extends IntentService {


    public AppLaunchBackgroundService() {
        super("AppLaunchBackgroundService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("AppLaunchBackgroundSer","handleintenet");
    }
}
