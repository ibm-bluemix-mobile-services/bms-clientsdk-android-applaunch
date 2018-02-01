package com.ibm.mobile.applaunch.android.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by norton on 12/28/17.
 */

public class AppLaunchAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.ibm.mobile.applaunch.service";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AppLaunchBackgroundService.class);
        context.startService(i);
    }
}
