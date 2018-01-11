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
