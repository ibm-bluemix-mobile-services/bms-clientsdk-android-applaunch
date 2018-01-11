package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 1/2/18.
 */

public enum RefreshPolicy {

    REFRESH_ON_EVERY_START(0),

    REFRESH_ON_EXPIRY(1),

    BACKGROUND_REFRESH(2);

    private int refresh_policy;

    RefreshPolicy(int  refresh_policy) {
        this.refresh_policy = refresh_policy;
    }

    public int getRefreshPolicy() {
        return refresh_policy;
    }
}
