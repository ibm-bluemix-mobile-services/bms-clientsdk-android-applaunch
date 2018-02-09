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
    }
}
