package com.applaunch.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applaunch.AppLaunchFailResponse;
import com.applaunch.AppLaunchResponse;
import com.applaunch.AppLaunchResponseListener;
import com.applaunch.R;
import com.applaunch.common.AppLaunchConstants;
import com.applaunch.common.AppLaunchProperties;
import com.applaunch.common.AppLaunchUtils;
import com.applaunch.logger.AppLaunchAnalytics;
import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.Analytics;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.applaunch.R.id.buttonpanel;
import static com.applaunch.common.AppLaunchConstants.ACTIONS_INVOKED;

/**
 * Created by norton on 7/21/17.
 */

public class AppLaunch {


    private AppLaunchProperties appLaunchProperties;
    public static final String PREFS_NAME = "com.ibm.mobile.services.engage";
    // public static final String URL = "http://10.0.2.2:8080/api/v1/";

    private String URL;
    private String ANALYZER_URL;
    private String appGUID;
    private String clientSecret;
    private String deviceId;
    private String userOSType = "android";
    private String userLocale;
    private AppLaunchConfig appLaunchConfig;
    private static final String APP_LAUNCH = "AppLaunch" ;
    private boolean isInitialized = false;
    private AppLaunchResponseListener myListener;
    SharedPreferences prefs = null;
    private JSONArray features = null;
    private boolean renderUi = true;
    private HashMap<String, JSONObject> featureList;
    private SharedPreferences sharedpreferences;
    private String actions=null;
    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunch.class.getSimpleName());

    private static Context appContext = null;

    private static AppLaunch thisInstance = null;

    private AppLaunch() {
        super();
        featureList = new HashMap<>();
    }

    public synchronized static AppLaunch getInstance() {
        if (thisInstance == null) {
            thisInstance = new AppLaunch();
        }
        return thisInstance;
    }

    /**
     * Initialize app details
     * @param context
     * @param appGuid
     * @param clientSecret
     * @param region
     */
    public void initApp(Application context,String region, String appGuid, String clientSecret){
        if (appGuid != null && context != null && clientSecret!=null && region!=null ) {
            this.appLaunchConfig = new AppLaunchConfig(context,region,appGuid,clientSecret);
            appContext = context;
            sharedpreferences = appLaunchConfig.getApplication().getSharedPreferences(APP_LAUNCH, Context.MODE_PRIVATE);
         //   isFirstTimeUser = sharedpreferences.getBoolean(AppLaunchConstants.FIRST_TIME_USER,true);
            BMSClient.getInstance().initialize(appLaunchConfig.getContext(), region);
            appLaunchProperties = AppLaunchProperties.getInstance(appLaunchConfig.getContext());
            URL = appLaunchProperties.getProtocol() + "://" + appLaunchProperties.getHost() + ":" + appLaunchProperties.getPort() + appLaunchProperties.getServerContext();
            ANALYZER_URL = appLaunchProperties.getAnalyzerProtocol() + "://" + appLaunchProperties.getAnalyzerServerHost() + ":" + appLaunchProperties.getAnalyzerServerPort() + appLaunchProperties.getAnalyzerServerContext();
            ANALYZER_URL += appLaunchConfig.getApplicationId();
            String user = sharedpreferences.getString(AppLaunchConstants.APP_USER,null);
            if(user!=null){
                appLaunchConfig.setUserID(user);
            }
            if(appLaunchConfig.getUserID()!=null && appLaunchConfig.getUserID().length()>0){
                ANALYZER_URL+="/users/"+ appLaunchConfig.getUserID();
                //override the default server url to send analytics information
                AppLaunchAnalytics.overrideServerHost = ANALYZER_URL;
            }

        }else{
            throw new RuntimeException("Invalid Init paramters");
        }
    }

    /**
     * @param userId
     * @param appLaunchResponseListener
     */
    public void registerUser(String userId, final AppLaunchResponseListener appLaunchResponseListener) {
        if(appLaunchResponseListener==null|| userId==null)
            throw new RuntimeException("AppLaunch:register() - arguments cannot be null.", null);
       register(userId,appLaunchResponseListener,null);
    }

    /**
     *
     * @param userId
     */
    public void registerUser(String userId){
        if(userId==null)
            throw new RuntimeException("AppLaunch:register() - userId cannot be null.", null);
        register(userId,null,null);
    }


    /**
     *
     * @param userId
     * @param parameters
     */
    public void registerUser(String userId,AppLaunchParameters parameters){
        if(userId==null|| parameters==null)
            throw new RuntimeException("AppLaunch:register() - arguemnts cannot be null.", null);
        register(userId,null,parameters.getParameters());
    }


    /**
     *
     * @param userId
     * @param key
     * @param value
     */
    public void registerUser(String userId,String key,String value){
        if(userId==null|| key==null||value==null)
            throw new RuntimeException("AppLaunch:register() - arguemnts cannot be null.", null);
        AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
        appLaunchParameters.put(key,value);
        register(userId,null,appLaunchParameters.getParameters());
    }


    /**
     *
     * @param userId
     * @param key
     * @param value
     * @param appLaunchResponseListener
     */
    public void registerUser(String userId,String key,String value,AppLaunchResponseListener appLaunchResponseListener){
        if(userId==null|| key==null||value==null || appLaunchResponseListener==null)
            throw new RuntimeException("AppLaunch:register() - arguemnts cannot be null.", null);
        AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
        appLaunchParameters.put(key,value);
        register(userId,appLaunchResponseListener,appLaunchParameters.getParameters());
    }



    /**
     * @param userId
     * @param parameters
     * @param appLaunchResponseListener
     */
    public void registerUser(String userId,AppLaunchParameters parameters,AppLaunchResponseListener appLaunchResponseListener) {
        if(appLaunchResponseListener==null || userId==null || parameters==null)
            throw new RuntimeException("AppLaunch:register() - arguments  cannot be null.", null);
        register(userId,appLaunchResponseListener,parameters.getParameters());
    }


    private void register(String userId,AppLaunchResponseListener appLaunchResponseListener, Hashtable parameters){
        try {
            if (null != appLaunchConfig && null != appLaunchConfig.getContext() && userId!=null) {
                //create a dummy listener if the object is null
                if(appLaunchResponseListener==null) {
                    appLaunchResponseListener = new AppLaunchResponseListener() {
                        @Override
                        public void onSuccess(AppLaunchResponse appLaunchResponse) {

                        }

                        @Override
                        public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {

                        }
                    };
                }
                appLaunchConfig.setUserID(userId);
                //proceed to registration only if the user is a new user
                if(sharedpreferences.getString(AppLaunchConstants.APP_USER,null)==null){
                    ANALYZER_URL+="/users/"+ appLaunchConfig.getUserID();
                    AppLaunchAnalytics.overrideServerHost = ANALYZER_URL;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(AppLaunchConstants.ANALYZER_URL,ANALYZER_URL);
                    editor.putString(AppLaunchConstants.APP_USER,userId);
                    editor.commit();
                    register(appLaunchResponseListener,parameters);
                }else{
                    //if the user is an already registered user return the cached registration response
                    String registrationResponse = sharedpreferences.getString(userId,"");
                    AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                    appLaunchResponse.setResponseText(registrationResponse);
                    appLaunchResponseListener.onSuccess(appLaunchResponse);
                }
            } else {
                logger.error("AppLaunch:register() - Invoke initApp() before register");
                throw new RuntimeException("AppLaunch:register() - Invoke initApp() before register or invalid parameters", null);
            }
        } catch (Exception e) {
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
            appLaunchFailResponse.setErrorMsg(e.getMessage());
            appLaunchResponseListener.onFailure(appLaunchFailResponse);
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service.");
            throw new RuntimeException(e);
        }
    }

    /**
     * @param parameters
     * @param appLaunchResponseListener
     */
    public void updateUser(AppLaunchParameters parameters, final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null == parameters || null == appLaunchResponseListener) {
                logger.error("AppLaunch:updateUser() -  arguments cannot be null");
                throw new RuntimeException("AppLaunch:updateUser() -  arguments cannot be null", null);
            }
            register(appLaunchResponseListener,parameters.getParameters());
        } catch (Exception e) {
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
            appLaunchFailResponse.setErrorMsg(e.getMessage());
            appLaunchResponseListener.onFailure(appLaunchFailResponse);
            logger.error("EngageCore:updateUser() - An error occured while updateUser EngageCore service.");
            throw new RuntimeException(e);
        }
    }

    /**
     * @param key
     * @param value
     */
    public void updateUser(String key,String value) {
        try {
            if (null == key || null == value) {
                logger.error("AppLaunch:updateUser() -  arguments cannot be null");
                throw new RuntimeException("AppLaunch:updateUser() -  arguments cannot be null", null);
            }
            AppLaunchParameters parameters = new AppLaunchParameters();
            parameters.put(key,value);
            register(null,parameters.getParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param key
     * @param value
     */
    public void updateUser(String key,String value, final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null == key || null == value || appLaunchResponseListener==null) {
                logger.error("AppLaunch:updateUser() -  arguments cannot be null");
                throw new RuntimeException("AppLaunch:updateUser() -  arguments cannot be null", null);
            }
            AppLaunchParameters parameters = new AppLaunchParameters();
            parameters.put(key,value);
            register(appLaunchResponseListener,parameters.getParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param appLaunchActions
     */
    public void getActions(final AppLaunchActions appLaunchActions) {
      if(appLaunchActions!=null){
          if (null != appLaunchConfig && null != appLaunchConfig.getContext()) {
              String actionsUrl = ANALYZER_URL + "/actions?deviceId="+ AppLaunchUtils.getDeviceId();
              Request getReq = new Request(actionsUrl, Request.GET);
              getReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

              getReq.send(appContext, new ResponseListener() {
                  @Override
                  public void onSuccess(Response response) {
                     actions = response.getResponseText();
                      if(actions!=null){
                          SharedPreferences.Editor editor = sharedpreferences.edit();
                          editor.putBoolean(ACTIONS_INVOKED,true);
                          editor.commit();
                          try {
                              JSONObject actionsObject = new JSONObject(actions);
                              JSONArray featuresArray =  actionsObject.getJSONArray("features");
                              //process features
                              processFeatures(featuresArray);
                              appLaunchActions.onFeaturesReceived(featuresArray.toString());
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                      }
                  }

                  @Override
                  public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                    Log.d("getActions",response.getResponseText());
                  }
              });
          }
          }
      }




    public boolean isFeatureEnabled(String featureCode) throws AppLaunchException{
        if(sharedpreferences.getBoolean(AppLaunchConstants.ACTIONS_INVOKED,false)){
            if (!featureList.isEmpty() && featureList.containsKey(featureCode)) {
                return true;
            }
            return false;
        }
         throw new AppLaunchException("Invoke getActions() api before isFeatureEnabled()");
    }

    /**
     * This api returns the variable for the feature code
     * @param featureCode
     * @param variableCode
     * @return
     */
    public String getPropertyOfFeature(String featureCode, String variableCode) throws AppLaunchException {
        if(sharedpreferences.getBoolean(AppLaunchConstants.ACTIONS_INVOKED,false)) {
            String returnValue = null;
            if (featureList.containsKey(featureCode)) {
                JSONObject featureObject = featureList.get(featureCode);
                try {
                    JSONArray variableArray = featureObject.getJSONArray("variables");
                    for (int index = 0; index < variableArray.length(); index++) {
                        try {
                            JSONObject variableObject = (JSONObject) variableArray.get(index);
                            if (variableObject.getString("code").equals(variableCode)) {
                                returnValue = variableObject.getString("value");
                                break;
                            }
                        } catch (JSONException ex) {
                            continue;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    returnValue = null;
                }
            }
            return returnValue;
        }else{
            throw new AppLaunchException("Invoke getActions() api before getPropertyOfFeature()");
        }
    }

    /**
     * Sends all the analytics event to the server
     */
    private void sendLogs(){
        AppLaunchAnalytics.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                if(response!=null)
                    Log.d("Sent logs successfully", response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                StringBuilder errorMessage = new StringBuilder();
                if(response!=null)
                    errorMessage.append("Send Logs" + " :" + response.getResponseText());
                if(t!=null){
                    errorMessage.append(":").append(t.getMessage());
                }
                if(extendedInfo!=null){
                    errorMessage.append(extendedInfo.toString());
                }
                Log.e("Failed Sending Logs", errorMessage.toString());
            }
        });
    }


    private void register(AppLaunchResponseListener appLaunchResponseListener, Hashtable parameters) throws JSONException, Exception{
        if (AppLaunchUtils.validateString(appLaunchConfig.getClientSecret()) && AppLaunchUtils.validateString(appLaunchConfig.getApplicationId()) && AppLaunchUtils.validateString(appLaunchConfig.getUserID())) {
            AppLaunchAnalytics.init(appLaunchConfig.getApplication(), appLaunchConfig.getUserID(), "", appLaunchConfig.getClientSecret(), true, Analytics.DeviceEvent.ALL);
            this.clientSecret = appLaunchConfig.getClientSecret();
            this.appGUID = appLaunchConfig.getApplicationId();
            //send all the analytics event to the server
            sendLogs();
            //add listener to track app events
            JSONObject initJson = AppLaunchUtils.getInitJson(appLaunchConfig.getApplication());
            if (initJson == null) {
                throw new RuntimeException("Error constructing register json body");
            }
            initJson.put("userId", appLaunchConfig.getUserID());
            if(parameters!=null && parameters.size()>0){
                Enumeration keys = parameters.keys();
                while(keys.hasMoreElements()){
                    String key = (String) keys.nextElement();
                    initJson.put(key,parameters.get(key));
                }
            }
            //construct initialze url
            String registrationUrl = URL + "apps/" + appLaunchConfig.getApplicationId() + "/users";
            //post the body to the server
            sendPostRequest("initialize", registrationUrl, initJson, appLaunchResponseListener);
        } else {
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value");
            System.out.print("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value");
            throw new Exception("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value", null);
        }
    }


    /**
     * The metric to be sent to the server
     *
     * @param metrics
     */
    public void sendMetrics(ArrayList<String> metrics) {
        String metricsUrl = ANALYZER_URL + "/events/metrics?deviceId="+ AppLaunchUtils.getDeviceId();;
        try {
            JSONObject metricJson = AppLaunchUtils.getMetricJson();
            JSONArray jsonArray = new JSONArray();
            if (metricJson == null|| metrics==null) {
                throw new RuntimeException("Error creating Metrics payload");
            }
            for(String metric:metrics){
                jsonArray.put(metric);
            }
            metricJson.put("userId", appLaunchConfig.getUserID());
            metricJson.put("metricCodes",jsonArray);
            sendPostRequest("SendMetrics", metricsUrl, metricJson, new AppLaunchResponseListener() {
                @Override
                public void onSuccess(AppLaunchResponse appLaunchResponse) {
                    Log.d("sendMetricsSuccess", appLaunchResponse.getResponseText());
                }

                @Override
                public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                    Log.d("sendMetricsFailure", appLaunchFailResponse.getErrorMsg());
                }
            });
        } catch (Exception ex) {
            logger.error("EngageCore:sendMetrics() - An error occured while sending metrics to server.");
        }

    }

    private void processFeatures(JSONArray features){
        if(features!=null){
            try {
                featureList.clear();
                for (int index = 0; index < features.length(); index++) {
                    JSONObject jsonObject = (JSONObject) features.get(index);
                    featureList.put(jsonObject.getString("code"), jsonObject);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    /**
     * @param appLaunchResponseListener
     */
    private void getFeatures(final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null != appLaunchConfig && null != appLaunchConfig.getContext()) {
                String featureUrl = ANALYZER_URL + "/actions/features";
                Request getReq = new Request(featureUrl, Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        String featureString = response.getResponseText();
                        if (null != featureString) {
                            try {
                                features = new JSONArray(featureString);
                                featureList.clear();
                                for (int index = 0; index < features.length(); index++) {
                                    JSONObject jsonObject = (JSONObject) features.get(index);
                                    featureList.put(jsonObject.getString("code"), jsonObject);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                        if(null!= appLaunchResponseListener){
                            AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                            appLaunchResponseListener.onSuccess(appLaunchResponse);
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != appLaunchResponseListener) {
                            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                            appLaunchResponseListener.onFailure(appLaunchFailResponse);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            features = null;
            logger.error("EngageCore:getFeatures() - An error occured fetching features.");
        }
    }


    /**
     * @param featureCode
     */
    private void isFeatureEnabled(final String featureCode,final AppLaunchResponseListener appLaunchResponseListener) {
        try {

            if (null != appLaunchConfig && null != appLaunchConfig.getContext() && null != featureCode && null!= appLaunchResponseListener) {
                String featureUrl = ANALYZER_URL + "/actions/features";
                Request getReq = new Request(featureUrl, Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        String featureString = response.getResponseText();
                        if (null != featureString) {
                            try {
                                features = new JSONArray(featureString);
                                featureList.clear();
                                for (int index = 0; index < features.length(); index++) {
                                    JSONObject jsonObject = (JSONObject) features.get(index);
                                    featureList.put(jsonObject.getString("code"), jsonObject);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                        if (featureList.containsKey(featureCode)) {
                            appLaunchResponseListener.onSuccess(new AppLaunchResponse());

                        } else {
                            appLaunchResponseListener.onFailure(new AppLaunchFailResponse());
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != appLaunchResponseListener) {
                            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                            appLaunchResponseListener.onFailure(appLaunchFailResponse);
                        }
                    }
                });
            }

        } catch (Exception ex) {
            logger.error("EngageCore:isFeatureEnabled() - An error occured fetching features.");
        }

    }




    /**
     * @param appLaunchConfig
     * @param appLaunchResponseListener
     */
    private void getThemes(AppLaunchConfig appLaunchConfig, final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null != appLaunchConfig && null != appLaunchConfig.getContext() && null != appLaunchResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                        appLaunchResponseListener.onSuccess(appLaunchResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchResponseListener.onFailure(appLaunchFailResponse);
                    }
                });

            }
        } catch (Exception ex) {

        }
    }


    /**
     * @param appLaunchConfig
     * @param appLaunchResponseListener
     */
    private void getCustomizations(AppLaunchConfig appLaunchConfig, final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null != appLaunchConfig && null != appLaunchConfig.getContext() && null != appLaunchResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                        appLaunchResponseListener.onSuccess(appLaunchResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchResponseListener.onFailure(appLaunchFailResponse);
                    }
                });

            }
        } catch (Exception ex) {

        }
    }

    /**
     * @param appLaunchConfig
     * @param appLaunchResponseListener
     */
    private void getMessages(AppLaunchConfig appLaunchConfig, final AppLaunchResponseListener appLaunchResponseListener) {
        try {
            if (null != appLaunchConfig && null != appLaunchConfig.getContext() && null != appLaunchResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                        appLaunchResponseListener.onSuccess(appLaunchResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchResponseListener.onFailure(appLaunchFailResponse);
                    }
                });

            }
        } catch (Exception ex) {

        }
    }


    private String getDeviceId() {
        return deviceId;
    }

    private String getUserLocale() {
        return userLocale;
    }


    /**
     * @param key
     * @param value
     * @param appLaunchResponseListener
     */
    private void updateUser(String key, int value, final AppLaunchResponseListener appLaunchResponseListener) {
        if (null != key && null != appLaunchResponseListener) {
            try {
                JSONObject jsonObject = AppLaunchUtils.getInitJson(appLaunchConfig.getApplication());
                jsonObject.put(key, value);
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                        appLaunchResponseListener.onSuccess(appLaunchResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchResponseListener.onFailure(appLaunchFailResponse);
                    }
                });
            } catch (JSONException e) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                appLaunchResponseListener.onFailure(appLaunchFailResponse);
                e.printStackTrace();
            }
        } else {
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
            appLaunchResponseListener.onFailure(appLaunchFailResponse);
        }

    }


    /**
     * @param key
     * @param value
     * @param appLaunchResponseListener
     */
    private void updateUser(String key, boolean value, final AppLaunchResponseListener appLaunchResponseListener) {
        if (null != key && null != appLaunchResponseListener) {
            try {
                JSONObject jsonObject = AppLaunchUtils.getInitJson(appLaunchConfig.getApplication());
                jsonObject.put(key, value);
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                        appLaunchResponseListener.onSuccess(appLaunchResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchResponseListener.onFailure(appLaunchFailResponse);
                    }
                });
            } catch (JSONException e) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                appLaunchResponseListener.onFailure(appLaunchFailResponse);
                e.printStackTrace();
            }
        } else {
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
            appLaunchResponseListener.onFailure(appLaunchFailResponse);
        }

    }


    private void sendPostRequest(final String methodName, String url, JSONObject body, final AppLaunchResponseListener appLaunchResponseListener) {

        Request postReq = new Request(url, Request.POST);
       // postReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

        Map<String, List<String>> headers = new HashMap<>();
        List<String> headerValues = new ArrayList<>();
        headerValues.add("application/json");
        headers.put("Content-Type", headerValues);
        List<String> secretValues = new ArrayList<>();
        secretValues.add(appLaunchConfig.getClientSecret());
        headers.put("clientSecret", secretValues);
       // headers.put("clientSecret",appLaunchConfig.getClientSecret());
        postReq.setHeaders(headers);

        postReq.send(appContext, body.toString(), new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("POST INVOKE FUNCTION::", response.getResponseText());
                AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                appLaunchResponse.setResponseText(response.getResponseText());
                if("initialize".equals(methodName) && sharedpreferences!=null){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(appLaunchConfig.getUserID(),response.getResponseText());
                    editor.commit();
                }
                appLaunchResponseListener.onSuccess(appLaunchResponse);
                //  responseListener.onSuccess("SUCCESS:: invoke function pushed");
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                StringBuilder errorMessage = new StringBuilder();
                if(response!=null)
                    errorMessage.append(methodName + " :" + response.getResponseText());
                if(t!=null){
                    errorMessage.append(":").append(t.getMessage());
                }
                if(extendedInfo!=null){
                    errorMessage.append(extendedInfo.toString());
                }
                appLaunchFailResponse.setErrorMsg(errorMessage.toString());
                appLaunchResponseListener.onFailure(appLaunchFailResponse);
                Log.d("POST INVOKE FUNCTION:: ", methodName + " " + errorMessage.toString());
                Log.d("Extended info:::", errorMessage.toString());
                //  responseListener.onFailure("FAILURE:: invoke function push failed");
            }
        });
    }

    // ********* //


    public void getFeatureToggle(final AppLaunchResponseListener responseListener) {

        // use bms core functionality to connect to db and fetch exiting on-boarding msgs for this user, if any...

        Request getReq = new Request(URL + "captivateengine/features", Request.GET);
        getReq.setQueryParameter("OSType", "android");
        getReq.setQueryParameter("locale", userLocale);
        getReq.setQueryParameter("serviceInstanceId", appGUID);
        getReq.setQueryParameter("deviceId", deviceId);

        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                //  responseListener.onSuccess(response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                // responseListener.onFailure(response.getResponseText());
            }
        });

    }

    public void getInAppMsgs(boolean autoRenderUI, final AppLaunchResponseListener responseListener) {

        // use bms core functionality to connect to db and fetch exiting in-app msgs for this user, if any...

        Request getReq = new Request(URL + "captivateengine/inappmsg", Request.GET);
        getReq.setQueryParameter("OSType", userOSType);
        getReq.setQueryParameter("locale", userLocale);
        getReq.setQueryParameter("serviceInstanceId", appGUID);
        getReq.setQueryParameter("deviceId", deviceId);
        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // responseListener.onSuccess(response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                //  responseListener.onFailure(response.getResponseText());
            }
        });
    }

    public void getAppTheme(final AppLaunchResponseListener responseListener) {

        // use bms core functionality to connect to db and fetch exiting push-notification msgs for this user, if any...

        Request getReq = new Request(URL + "captivateengine/apptheme", Request.GET);
        getReq.setQueryParameter("OSType", userOSType);
        getReq.setQueryParameter("locale", userLocale);
        getReq.setQueryParameter("serviceInstanceId", appGUID);
        getReq.setQueryParameter("deviceId", deviceId);
        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                //   responseListener.onSuccess(response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                //   responseListener.onFailure(response.getResponseText());
            }
        });
    }

    private void getMessages(final Context context, final AppLaunchResponseListener appLaunchResponseListener){

        String messageUrl = ANALYZER_URL+"/actions/messages";
        Request getReq = new Request(messageUrl, Request.GET);
        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("getMessages",response.getResponseText());
                if(renderUi){
                    try{
                        String jsonResponse="{\n" +
                                "  \"inApp\": [\n" +
                                "  {\n" +
                                "    \"name\": \"bannermessage\",\n" +
                                "    \"layout\": \"banner\",\n" +
                                "    \"title\": \"Hey there banner\",\n" +
                                "    \"subtitle\": \"Welcome banner\",\n" +
                                "    \"imageUrl\": \"https://goo.gl/bktLai\",\n" +
                                "    \"customConfig\": {\n" +
                                "      \"name\": \"voice\"\n" +
                                "    },\n" +
                                "    \"buttons\": [{\n" +
                                "      \"name\": \"Ok\",\n" +
                                "      \"action\": \"okaction\",\n" +
                                "      \"metrics\": [\n" +
                                "      {\n" +
                                "        \"name\": \"bannermessage\",\n" +
                                "        \"code\": \"0x45454\"\n" +
                                "      }, \n" +
                                "      {\n" +
                                "        \"name\": \"message\",\n" +
                                "        \"code\": \"0x45654\"\n" +
                                "      }\n" +
                                "      ]\n" +
                                "    },{\n" +
                                "      \"name\": \"Cancel\",\n" +
                                "      \"action\": \"cancelaction\",\n" +
                                "      \"metrics\": [\n" +
                                "      {\n" +
                                "        \"name\": \"cancelmessage\",\n" +
                                "        \"code\": \"0x45454\"\n" +
                                "      }, \n" +
                                "      {\n" +
                                "        \"name\": \"cancel\",\n" +
                                "        \"code\": \"0x45654\"\n" +
                                "      }\n" +
                                "      ]\n" +
                                "    }]\n" +
                                "  }\n" +
                                "  ],\n" +
                                "  \"permissions\": [],\n" +
                                "  \"carousel\": []\n" +
                                "}";
                        JSONObject messageJson = new JSONObject(response.getResponseText());
                        processInAppMessages(context,messageJson);
                    }catch (Exception ex){
                        AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                        appLaunchFailResponse.setErrorMsg("Error parsing response: " + ex.getMessage());
                        //  engageResponseListener.onFailure(engageFailResponse);
                        ex.printStackTrace();
                    }

                }else{
                    AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                    appLaunchResponse.setResponseText(response.getResponseText());
                    appLaunchResponseListener.onSuccess(appLaunchResponse);
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                if(response!=null){
                    Log.d("getMessages",response.getResponseText());
                    AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                    appLaunchFailResponse.setErrorMsg(response.getResponseText());
                    appLaunchResponseListener.onFailure(appLaunchFailResponse);
                }
            }
        });
    }



    private void processInAppMessages(final Context context,JSONObject messageJson){
        if(messageJson!=null) {
            try {
                JSONArray inAppMsgList = messageJson.getJSONArray("inApp");
                if (null != inAppMsgList && inAppMsgList.length() > 0) {
                    for (int inappIndex = 0; inappIndex < inAppMsgList.length(); inappIndex++) {
                        JSONObject inappMessage = inAppMsgList.getJSONObject(inappIndex);
                        String layout = inappMessage.getString("layout");
                        if (MessageTypes.BANNER.equals(layout)) {
                            final MessageData messageData = new MessageData(MessageTypes.BANNER);
                            messageData.setTitle(inappMessage.getString("title"));
                            messageData.setSubTitle(inappMessage.getString("subtitle"));
                            messageData.setImageUrl(inappMessage.getString("imageUrl"));
                            //process the buttons
                            processButtons(messageData,inappMessage);
                            //display the message
                            if(context!=null){
                                ((Activity)context).runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        displayBannerDialog(context, messageData);
                                    }
                                });
                            }
                        } else if (MessageTypes.TOP_SLICE.equals(layout)) {

                        } else if (MessageTypes.BOTTOM_PANEL.equals(layout)) {

                        }
                    }
                }
            } catch (JSONException e) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                appLaunchFailResponse.setErrorMsg("Error parsing response: " + e.getMessage());
                //  engageResponseListener.onFailure(engageFailResponse);
                e.printStackTrace();
            }
        }
    }


    private void processButtons(MessageData messageData, JSONObject inappMessage){
        try {
            JSONArray buttonArray = inappMessage.getJSONArray("buttons");
            if (null != buttonArray && buttonArray.length() > 0) {
                for (int buttonIndex = 0; buttonIndex < buttonArray.length(); buttonIndex++) {
                    JSONObject buttonObject = buttonArray.getJSONObject(buttonIndex);
                    ButtonData buttonData = new ButtonData();
                    buttonData.setButtonName(buttonObject.getString("name"));
                    buttonData.setAction(buttonObject.getString("action"));
                    buttonData.setMetrics(buttonObject.getJSONArray("metrics"));
                    messageData.addButton(buttonData);
                }
            }
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    private void processPermissions(JSONObject messageJson){
        try{
            JSONArray permissionsMsgList = messageJson.getJSONArray("permissions");
            if(null!=permissionsMsgList && permissionsMsgList.length()>0){

            }
        }catch (Exception ex){

        }
    }

    private void processCarousal(JSONObject messageJson){
        try{
            JSONArray carouselMsgList = messageJson.getJSONArray("carousel");

            if(null!=carouselMsgList && carouselMsgList.length()>0){

            }
        }catch (Exception ex){

        }
    }


    private void displayBannerDialog(Context context,MessageData messageData){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogLayout = inflater.inflate(R.layout.banner_dialog_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImageView image = (ImageView) dialogLayout.findViewById(R.id.goProDialogImage);
        Picasso.with(context).load(messageData.getImageUrl()).placeholder(R.drawable.placeholder_image).into(image);
        TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
        titleView.setText(messageData.getTitle());
        TextView subTitleView = (TextView) dialogLayout.findViewById(R.id.subtitle);
        subTitleView.setText(messageData.getSubTitle());
        LinearLayout buttonPanel = (LinearLayout) dialogLayout.findViewById(buttonpanel);
        ArrayList<ButtonData> buttonDataList = messageData.getButtonDataList();
        for(ButtonData buttonData : buttonDataList) {
            Button dialogButton = new Button(context);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 1;
            dialogButton.setLayoutParams(p);
            buttonPanel.addView(dialogButton);
            dialogButton.setTag(buttonData);
            dialogButton.setText(buttonData.getButtonName());
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ButtonData buttonDataTag = (ButtonData) v.getTag();
                    JSONArray metricsList = buttonDataTag.getMetrics();
                    ArrayList<String> metricCodes = new ArrayList<String>();
                    for (int i = 0; i < metricsList.length(); i++) {
                        try {
                            JSONObject metricObject = (JSONObject) metricsList.get(i);
                            metricCodes.add(metricObject.getString("code"));
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                    }
                    sendMetrics(metricCodes);
                    dialog.dismiss();
                }
            });

        }
        dialog.show();
    }



    public void getJsonConfig(final AppLaunchResponseListener responseListener) {

        // use bms core functionality to connect to db and fetch exiting config msgs for this user, if any...

        Request getReq = new Request(URL + "captivateengine/jsonconfig", Request.GET);
        getReq.setQueryParameter("OSType", userOSType);
        getReq.setQueryParameter("locale", userLocale);
        getReq.setQueryParameter("serviceInstanceId", appGUID);
        getReq.setQueryParameter("deviceId", deviceId);
        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                //  responseListener.onSuccess(response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                //  responseListener.onFailure(response.getResponseText());
            }
        });
    }

    public void updateUser(final AppLaunchResponseListener responseListener) {

        // use bms core functionality to connect to db and fetch exiting config msgs for this user, if any...

        Request getReq = new Request(URL + "", Request.GET);
        getReq.setQueryParameter("OSType", userOSType);
        getReq.setQueryParameter("locale", userLocale);
        getReq.setQueryParameter("serviceInstanceId", appGUID);
        getReq.setQueryParameter("deviceId", deviceId);
        getReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // responseListener.onSuccess(response.getResponseText());
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                //  responseListener.onFailure(response.getResponseText());
            }
        });
    }

    public void putFunctionTrigger(final String functionName, final AppLaunchResponseListener responseListener) throws JSONException {

        Request postReq = new Request(URL + "buttonconfig", Request.POST);

        Map<String, List<String>> headers = new HashMap<>();
        List<String> headerValues = new ArrayList<>();
        headerValues.add("application/json");

        headers.put("Content-Type", headerValues);

        postReq.setHeaders(headers);

        JSONObject postObj = new JSONObject();
        postObj.put("buttonName", functionName);
        postObj.put("serviceInstanceId", appGUID);
        postObj.put("buttonType", "invoke-function");
        postObj.put("buttonValue", functionName);

        postReq.send(appContext, postObj.toString(), new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("POST INVOKE FUNCTION::", "pushed " + functionName + " invoke function");
                //  responseListener.onSuccess("SUCCESS:: invoke function pushed");
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                Log.d("POST INVOKE FUNCTION::", " failed to push " + functionName + " invoke function");
                Log.d("Extended info:::", response.getResponseText());
                Log.d("Extended info:::", t.getMessage());
                Log.d("Extended info:::", extendedInfo.toString());
                //  responseListener.onFailure("FAILURE:: invoke function push failed");
            }
        });
//
//        // use bms core functionality to connect to db and fetch exiting config msgs for this user, if any...
//        Request getReq = new Request(URL+"buttonconfig", Request.GET);
//        getReq.setQueryParameter("instanceid",serviceInstanceId);
//        getReq.setQueryParameter("type","invoke-function");
//        getReq.send(appContext, new ResponseListener() {
//            @Override
//            public void onSuccess(Response response) {
//                try {
//                    Log.d("sdk",response.getResponseText());
//                    JSONArray obj = new JSONArray(response.getResponseText());
//                    if(obj.length() == 0){
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
//                Log.d("GET INVOKE FUNCTION::", " failed to make GET call");
//            }
//        });
    }

    public void showDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater)appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setView(layout);
        adb.show();
    }
}
