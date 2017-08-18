package com.engage;



/**
 * Created by norton on 7/25/17.
 */

public interface EngageResponseListener {
    public void onSuccess(EngageResponse engageResponse);
    public void onFailure(EngageFailResponse engageFailResponse);
}
