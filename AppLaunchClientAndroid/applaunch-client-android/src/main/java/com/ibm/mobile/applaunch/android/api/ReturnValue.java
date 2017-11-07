package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 8/4/17.
 */

class ReturnValue {

    private boolean returnValue;

    protected ReturnValue() {
        super();
    }

    protected boolean returnValue() {
        return returnValue;
    }

    protected void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }
}
