package com.ibm.mobile.applaunch.android.common;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;

/**
 * Created by norton on 7/21/17.
 */

public class AppLaunchUtils {

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunchUtils.class.getSimpleName());
    private static String deviceId = null;

    public static final File getNoBackupFilesDir(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT <= LOLLIPOP_MR1)
            return ctx.getFilesDir();
        else
            return ctx.getNoBackupFilesDir();
    }

    public static Boolean validateString(String object) {
        if (object == null || object.isEmpty() || object == "") {
            return false;
        } else {
            return true;
        }
    }

    public static String getDeviceId() {
        if (deviceId == null) {
            logger.debug("Computing device ID");
            AuthorizationManager authorizationManager = BMSClient.getInstance().getAuthorizationManager();
            deviceId = authorizationManager.getDeviceIdentity().getId();
            logger.debug("DeviceId obtained from AuthorizationManager is : " + deviceId);
        }
        return deviceId;
    }

    public static String computeLocale() {
        return Locale.getDefault().getLanguage();
    }


    private static String getBrand() {
        return Build.MANUFACTURER + Build.BRAND;
    }

    private static String getModel() {
        return Build.MODEL;
    }


    private static int getOSVersion() {
        return Build.VERSION.SDK_INT;
    }

    private static String getPlatform() {
        // return Locale.getDefault().getLanguage();
        return "android";
    }

    public static JSONObject getInitJson(Application application) {
        JSONObject initObject = new JSONObject();
        try {
          //  initObject.put("model", getModel());
          //  initObject.put("brand", getBrand());
         //   initObject.put("OSVersion", getOSVersion());
            initObject.put("platform", getPlatform());
          //  initObject.put("appId", getPackageName(application));
         //   initObject.put("appVersion", "1.0.0");
         //   initObject.put("appName", getAppName(application));
        } catch (JSONException e) {
            initObject = null;
            logger.error("Error creating init json " + e.getMessage());
            e.printStackTrace();
        }
        return initObject;
    }

    public static String getAppName(Application application) {
        PackageManager pm = application.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(getPackageName(application), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }



    public static String getPackageName(Application application){
     return application.getApplicationInfo().packageName;
    }


    public boolean validateCustomParams(Object cachedValue, Object newValue){
        boolean returnValue =false;
        if(cachedValue instanceof String && newValue instanceof  String){
            String currentValue = (String) cachedValue;

        }
        return returnValue;

    }


    public static JSONObject getSessionJson(){
        JSONObject initObject = new JSONObject();
        try {
            initObject.put("model", getModel());
            initObject.put("brand", getBrand());
            initObject.put("OSVersion", getOSVersion());
            initObject.put("platform", getPlatform());
            initObject.put("deviceId", getDeviceId());
            initObject.put("appId","com.something.com");
            initObject.put("appVersion","1.0.0");
            initObject.put("appName","Testing");
        } catch (JSONException e) {
            initObject =null;
            logger.error("Error creating init json "+e.getMessage());
            e.printStackTrace();
        }
        return initObject;
    }


    public static JSONObject getMetricJson(){
        JSONObject metricObject = new JSONObject();
        try {
            metricObject.put("deviceId", getDeviceId());
        } catch (JSONException e) {
            metricObject = null;
            e.printStackTrace();
        }
        return  metricObject;
    }

}
