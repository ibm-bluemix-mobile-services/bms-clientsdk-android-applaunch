package com.ibm.mobile.applaunch.android;

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

/**
 * Created by norton on 7/26/17.
 */

public class AppLaunchFailResponse {

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunchResponse.class.getSimpleName());
    public static final String JSON_KEY_FAILURES = "failures";
    public static final int HTTP_STATUS = -1;
    private static final String JSON_KEY_ERROR_MSG  = "errorMsg";
    private static final String JSON_KEY_ERROR_CODE = "errorCode";
    private String errorCode;
    private String errorMsg;


    public AppLaunchFailResponse() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
