package com.ibm.mobile.applaunch.android.actions;

/**
 * Created by norton on 10/26/17.
 */

public class Actions {

    public static Actions thisInstance =null;

    private Actions() {
    }

    public static Actions getInstance(){
        if(thisInstance==null){
            thisInstance = new Actions();
        }
        return  thisInstance;
    }

}
