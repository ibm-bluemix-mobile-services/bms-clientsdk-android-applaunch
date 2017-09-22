package com.applaunch.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by norton on 7/21/17.
 */

public class AppLaunchProperties {


    private Properties engageProperties = new Properties();
    private Context con;
    private static AppLaunchProperties thisInstance = null;


    private AppLaunchProperties(Context context) {

        if (!isBOMPresent(context)) {
            try {
                engageProperties.load(context.getAssets().open(AppLaunchConstants.ENGAGE_CLIENT_PROPS_NAME));

                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                con = context;

                if (getHost() == null || getHost().isEmpty()) {
                    throw new RuntimeException("You must specify the server host (engageServerHost) in the client configuration file (" + AppLaunchConstants.ENGAGE_CLIENT_PROPS_NAME + ").");
                }
            } catch (IOException e) {
                throw new RuntimeException("Client configuration file " + AppLaunchConstants.ENGAGE_CLIENT_PROPS_NAME + " not found in application assets.");

            } catch (PackageManager.NameNotFoundException e) {
                throw new Error(e);
            }
        } else {
            throw new RuntimeException("Client configuration file " + AppLaunchConstants.ENGAGE_CLIENT_PROPS_NAME + " contains a BOM (Byte Order Mark). Save the file without a BOM");
        }

    }

    public static synchronized AppLaunchProperties getInstance(Context context) {
        if (thisInstance == null) {
            thisInstance = new AppLaunchProperties(context.getApplicationContext());
        }
        return thisInstance;
    }

    private boolean isBOMPresent(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(AppLaunchConstants.ENGAGE_CLIENT_PROPS_NAME);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            // BOM marker will only appear at the very beginning
            br.mark(4);
            if ('\ufeff' == br.read()) {
                return true;
            } else {
                br.reset(); // not the BOM marker
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public String getProtocol() {
        return engageProperties.getProperty(AppLaunchConstants.ENGAGE_SERVER_PROTOCOL);
    }

    public String getHost() {
        return engageProperties.getProperty(AppLaunchConstants.ENGAGE_SERVER_HOST);
    }

    public String getPort() {
        return engageProperties.getProperty(AppLaunchConstants.ENGAGE_SERVER_PORT);
    }

    public String getServerContext() {
        return engageProperties.getProperty(AppLaunchConstants.ENGAGE_SERVER_CONTEXT);
    }



    public String getAnalyzerProtocol() {
        return engageProperties.getProperty(AppLaunchConstants.ANALYZER_SERVER_PROTOCOL);
    }

    public String getAnalyzerServerHost() {
        return engageProperties.getProperty(AppLaunchConstants.ANALYZER_SERVER_HOST);
    }

    public String getAnalyzerServerPort() {
        return engageProperties.getProperty(AppLaunchConstants.ANALYZER_SERVER_PORT);
    }

    public String getAnalyzerServerContext() {
        return engageProperties.getProperty(AppLaunchConstants.ANALYZER_SERVER_CONTEXT);
    }
}
