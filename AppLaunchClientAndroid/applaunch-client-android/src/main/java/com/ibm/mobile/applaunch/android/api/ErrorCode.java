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

package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 1/3/18.
 */

/**
 * ErrorCode is an enumerator which contains error information.
 */
public enum ErrorCode {

    /**
     * Internal Error in the SDK.
     */
    INTERNAL_ERROR(-1),

    /**
     * Failed to register with Applaunch Service.
     */
    REGISTRATION_FAILURE(0),

    /**
     * Failed to retreive engagements from Applaunch Service.
     */
    FETCH_ACTIONS_FAILURE(1),

    /**
     * Failed to process engagements from Applaunch Service.
     */
    PROCESS_ACTIONS_FAILURE(1),

    /**
     * Failed to load default features from Application.
     */
    DEFAULT_FEATURE_LOAD_FAILURE(3),

    /**
     * Failed to unregister with Applaunch Service.
     */
    UNREGISTRATION_FAILURE(4);

    private int error_code;

    ErrorCode(int  error_code) {
        this.error_code = error_code;
    }

    public int getErrorCode() {
        return error_code;
    }
}
