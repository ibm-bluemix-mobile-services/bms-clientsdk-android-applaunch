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
<<<<<<< HEAD
=======

package com.ibm.mobile.applaunch.android.api;
>>>>>>> master

package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 9/22/17.
 */

/**
 * This is the callback for AppLaunch SDK APIs.
 */
public interface AppLaunchListener {

    /**
     * This method executes during the success scenario.
     *
     * @param response AppLaunchResponse Object
     */
    public void onSuccess(AppLaunchResponse response);

    /**
     * This method executes during the failure scenario.
     *
     * @param failResponse AppLaunchFailResponse Object
     */
    public void onFailure(AppLaunchFailResponse failResponse);
}
