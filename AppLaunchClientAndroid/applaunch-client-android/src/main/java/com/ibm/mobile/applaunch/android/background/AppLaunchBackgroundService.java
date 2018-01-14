package com.ibm.mobile.applaunch.android.background;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ibm.mobile.applaunch.android.common.AppLaunchConstants;

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
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(AppLaunchConstants.ACTIONS_RECEIVED_RECEIVER);
        sendBroadcast(broadcastIntent);
      //  AppLaunch.getInstance().
    }
}
