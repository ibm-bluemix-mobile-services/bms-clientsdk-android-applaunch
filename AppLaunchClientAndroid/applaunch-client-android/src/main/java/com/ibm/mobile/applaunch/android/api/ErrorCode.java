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

public enum ErrorCode {

    INTERNAL_ERROR(-1),

    REGISTRATION_FAILURE(0),

    FETCH_ACTIONS_FAILURE(1),

    PROCESS_ACTIONS_FAILURE(1),

    DEFAULT_FEATURE_LOAD_FAILURE(3),

    UNREGISTRATION_FAILURE(4);

    private int error_code;

    ErrorCode(int  error_code) {
        this.error_code = error_code;
    }

    public int getErrorCode() {
        return error_code;
    }
}
