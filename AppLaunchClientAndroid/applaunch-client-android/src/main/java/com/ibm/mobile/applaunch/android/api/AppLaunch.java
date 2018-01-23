package com.ibm.mobile.applaunch.android.api;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibm.mobile.applaunch.android.AppLaunchFailResponse;
import com.ibm.mobile.applaunch.android.AppLaunchResponse;
import com.ibm.mobile.applaunch.android.AppLaunchResponseListener;
import com.ibm.mobile.applaunch.android.R;
import com.ibm.mobile.applaunch.android.background.AppLaunchAlarmReceiver;
import com.ibm.mobile.applaunch.android.common.AppLaunchConstants;
import com.ibm.mobile.applaunch.android.common.AppLaunchUtils;
import com.ibm.mobile.applaunch.android.internal.AppLaunchUrlBuilder;
import com.ibm.mobile.applaunch.android.logger.AppLaunchAnalytics;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.ibm.mobile.applaunch.android.R.id.buttonpanel;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.ACTIONS;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.ACTIONS_INVOKED;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.ACTIONS_LAST_REFRESH;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.ACTIONS_RECEIVED_RECEIVER;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.ANALYZER_URL;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.CODE;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.INAPP_MESSAGES;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.PROPERTIES;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.TRIGGER_EVERY_ALTERNATE_LAUNCH;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.TRIGGER_EVERY_LAUNCH;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.TRIGGER_FIRST_LAUNCH;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.TRIGGER_ONCE_AND_ONLY_ONCE;
import static com.ibm.mobile.applaunch.android.common.AppLaunchConstants.VALUES;

/**
 * Created by norton on 7/21/17.
 */

public class AppLaunch {


    public static final String PREFS_NAME = "com.ibm.mobile.services.engage";
    private String appGUID;
    private String clientSecret;
    private String deviceId;
    private String userOSType = "android";
    private String userLocale;
    private AppLaunchUrlBuilder appLaunchUrlBuilder;
    private AppLaunchConfig appLaunchConfig;
   // private AppLaunchUser appLaunchUser;

    private AppLaunchCacheManager appLaunchCacheManager=null;
    private AppLaunchResponseListener myListener;
    SharedPreferences prefs = null;
    private JSONArray features = null;
    private boolean renderUi = true;
    private HashMap<String, JSONObject> featureList;

    private String actions=null;
    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AppLaunch.class.getSimpleName());

    private Application appContext = null;

    private HashMap<String,MessageData> messageList;

    private static AppLaunch thisInstance = null;

    private AppLaunch() {
        super();
        featureList = new HashMap<>();
        messageList = new HashMap<>();
        appLaunchCacheManager = AppLaunchCacheManager.getInstance();
    }

    public synchronized static AppLaunch getInstance() {
        if (thisInstance == null) {
            thisInstance = new AppLaunch();
         }
        return thisInstance;
    }


    /**
     *
     * @param context
     * @param region
     * @param appGuid
     * @param clientSecret
     * @param config
     * @param user
     * @param appLaunchActionListener
     */
    public void init(Application context,ICRegion region, String appGuid, String clientSecret, AppLaunchConfig config, AppLaunchUser user, AppLaunchListener appLaunchActionListener){
        if (appGuid != null && context != null && clientSecret!=null && region!=null && user.getUserId()!=null ) {
            appContext = context;
            //if app launch listener ==null create a dummy action listener
            if(appLaunchActionListener==null) {
                appLaunchActionListener = new AppLaunchListener() {
                    @Override
                    public void onSuccess(AppLaunchResponse response) {

                    }

                    @Override
                    public void onFailure(AppLaunchFailResponse failResponse) {

                    }
                };
            }
            BMSClient.getInstance().initialize(appContext, region.toString());
            appLaunchCacheManager.initializeCache(context);
            appLaunchConfig = config;
            //set all values into applaunchconfig internal methods to avoid global variable declarations
            appLaunchConfig.setBluemixRegion(region.toString());
            appLaunchConfig.setApplicationId(appGuid);
            appLaunchConfig.setClientSecret(clientSecret);
            appLaunchUrlBuilder = new AppLaunchUrlBuilder(region,appGuid,appLaunchConfig.getDeviceId(),user.getUserId());
            //load default feature for the app
            appLaunchCacheManager.loadDefaultFeatures(appLaunchActionListener);

            String userId = appLaunchCacheManager.getString(AppLaunchConstants.APP_USER,null);
            AppLaunchAnalytics.overrideServerHost = appLaunchUrlBuilder.getAnalyzerURL();
            //registerDevice the user
            registerDevice(user,appLaunchActionListener);
            //fetch actions from the server

        }else{
            throw new RuntimeException("Invalid Init paramters");
        }
    }

    /**
     *
     * @param appLaunchListener
     */
    public void destroy(final AppLaunchListener appLaunchListener){
        //TODO : Cache Clearing Mechanism and Check device is registered or not
            //send all the analytics event to the server
            sendLogs();
            //construct registration url
            String registrationUrl = appLaunchUrlBuilder.getAppRegistrationURL();
            //post the body to the server
            AppLaunchInternalListener appLaunchInternalListener = new AppLaunchInternalListener() {
                @Override
                public void onSuccess(AppLaunchResponse appLaunchResponse) {
                    // Clear all the cache
                    appLaunchCacheManager.destroyCache();
                }

                @Override
                public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                    appLaunchListener.onFailure(appLaunchFailResponse);
                }
            };
            if(appLaunchCacheManager.getString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),null)!=null){
                sendDeleteRequest(registrationUrl, appLaunchInternalListener);
            }
    }


    private void registerDevice(AppLaunchUser user,AppLaunchListener appLaunchActionListener) {
        if(user.getUserId()==null)
            throw new RuntimeException("AppLaunch:init() - arguments cannot be null.", null);
            registerDevice(user.getUserId(),appLaunchActionListener,user.getParameters());
    }


    /**
     *
     * @param userId
     */
    private void registerDevice(String userId){
        if(userId==null)
            throw new RuntimeException("AppLaunch:registerDevice() - userId cannot be null.", null);
        registerDevice(userId,null,null);
    }


    /**
     *
     * @param userId
     * @param parameters
     */
    private void registerDevice(String userId, Hashtable parameters){
        if(userId==null|| parameters==null)
            throw new RuntimeException("AppLaunch:registerDevice() - arguemnts cannot be null.", null);
        registerDevice(userId,null,parameters);
    }


    private void registerDevice(String userId, AppLaunchListener appLaunchListener, Hashtable parameters){
        try {
            if (null != appLaunchConfig && null != appContext && userId!=null) {

                appLaunchConfig.setUserID(userId);
                //proceed to registration only if the user is a new user
                if(appLaunchCacheManager.getString(userId+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),null)==null){
                  //  AppLaunchAnalytics.overrideServerHost = ANALYZER_URL;
                    register(appLaunchListener,parameters,false);
                }else{
                    //if the user is an already registered user return the cached registration response
                    String registrationResponse = appLaunchCacheManager.getString(userId+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),"");
                    String cachedParams = appLaunchCacheManager.getString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId()+"-params","");
                    boolean shouldUpdateUser=false;
                    JSONObject cachedParamJson = new JSONObject(cachedParams);
                    Enumeration paramKeys = parameters.keys();
                    while (paramKeys.hasMoreElements()){
                        String key = (String) paramKeys.nextElement();
                        if(!cachedParamJson.has(key)){
                            shouldUpdateUser = true;
                            break;
                        }
                    }
                    if(shouldUpdateUser){
                        register(appLaunchListener,parameters,true);
                        //invoke update user request
                    }else{
                        loadActions(appLaunchListener);
                    }
                }
            } else {
                logger.error("AppLaunch:registerDevice() - Invoke initApp() before registerDevice");
                throw new RuntimeException("AppLaunch:registerDevice() - Invoke initApp() before registerDevice or invalid parameters", null);
            }
        } catch (Exception e) {
            AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.REGISTRATION_FAILURE,e.getMessage());
            appLaunchListener.onFailure(appLaunchFailResponse);
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service.");
           // throw new RuntimeException(e);
        }
    }


    private void loadActions(final AppLaunchListener appLaunchListener){
        //refresh actions on every start of the app
        if(RefreshPolicy.REFRESH_ON_EVERY_START.equals(appLaunchConfig.getRefreshPolicy())){
            refreshActions(appLaunchListener);
        }else if(RefreshPolicy.REFRESH_ON_EXPIRY.equals(appLaunchConfig.getRefreshPolicy())){
            //refresh only if cache is expired
            long actionsLastRefresh = appLaunchCacheManager.getLong(ACTIONS_LAST_REFRESH,new Date().getTime());
            Date lastRefresh = new Date();
            lastRefresh.setTime(actionsLastRefresh);
            Date currentTime = new Date();
            long cacheExpirationTime = appLaunchConfig.getCacheExpiration()*1000;
            long timeLapsed = currentTime.getTime()-lastRefresh.getTime();
            if(timeLapsed>cacheExpirationTime){
                refreshActions(appLaunchListener);
            }
        }else if(RefreshPolicy.BACKGROUND_REFRESH.equals(appLaunchConfig.getRefreshPolicy())){
           scheduleAlarm((int) appLaunchConfig.getCacheExpiration());
            IntentFilter actionsReceivedIntent = new IntentFilter();
            actionsReceivedIntent.addAction(ACTIONS_RECEIVED_RECEIVER);
            appContext.registerReceiver(acionsReceiver,actionsReceivedIntent);
        }else{
            //return the cached actions response back since nothing has changed since the previous call
            try{
                //load inapp messages from cache into memory
                String inappMessageString = appLaunchCacheManager.getString(INAPP_MESSAGES,"");
                if(inappMessageString!=null && inappMessageString.length()>0){
                    JSONArray inappMessageList = new JSONArray(inappMessageString);
                    processInAppMessages(inappMessageList);
                }
                AppLaunchResponse actionsResponse = new AppLaunchResponse();
                String cachedActionsResponse = appLaunchCacheManager.getString(ACTIONS,"");
                actionsResponse.setResponseJSON(new JSONObject(cachedActionsResponse));
                appLaunchListener.onSuccess(actionsResponse);
            }catch (Exception ex){
                AppLaunchFailResponse applaunchFailResponse = new AppLaunchFailResponse(ErrorCode.PROCESS_ACTIONS_FAILURE,ex.getMessage());
                appLaunchListener.onFailure(applaunchFailResponse);
            }
        }


    }


    private BroadcastReceiver acionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshActions(new AppLaunchListener() {
                @Override
                public void onSuccess(AppLaunchResponse response) {
                    Log.i("Refresh Successful",response.getResponseJSON().toString());
                }

                @Override
                public void onFailure(AppLaunchFailResponse failResponse) {
                    Log.i("Refresh Failure", failResponse.getErrorMsg());
                }
            });
        }
    };

    private void refreshActions(final AppLaunchListener appLaunchListener){
        getActions(new AppLaunchInternalListener() {
            @Override
            public void onSuccess(AppLaunchResponse appLaunchResponse) {
                appLaunchListener.onSuccess(appLaunchResponse);
            }

            @Override
            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                AppLaunchFailResponse applaunchActionsFail = new AppLaunchFailResponse(ErrorCode.FETCH_ACTIONS_FAILURE,appLaunchFailResponse.getErrorMsg());
                appLaunchListener.onFailure(applaunchActionsFail);
            }
        });
    }

    /**
     * @param appLaunchActions
     */
    private void getActions(final AppLaunchInternalListener appLaunchActions) {
      if(appLaunchActions!=null){
          if (null != appLaunchConfig && null != appContext) {

             // String actionsUrl = ANALYZER_URL+"/users/"+ appLaunchConfig.getUserID() + "/actions?deviceId="+ AppLaunchUtils.getDeviceId();
              final String actionsUrl = appLaunchUrlBuilder.getActionURL();
              Request getReq = new Request(actionsUrl, Request.GET);
              getReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

              getReq.send(appContext, new ResponseListener() {
                  @Override
                  public void onSuccess(Response response) {
                     actions = response.getResponseText();
                      if(actions!=null){
                          appLaunchCacheManager.addBoolean(ACTIONS_INVOKED,true);
                          appLaunchCacheManager.addLong(ACTIONS_LAST_REFRESH,new Date().getTime());
                          appLaunchCacheManager.addString(ACTIONS,actions);
                         /* SharedPreferences.Editor editor = sharedpreferences.edit();
                          editor.putBoolean(ACTIONS_INVOKED,true);
                          editor.commit();*/
                          try {
                              JSONObject actionsObject = new JSONObject(actions);
                              JSONArray featuresArray =  actionsObject.getJSONArray("features");
                              //process features
                              processFeatures(featuresArray);
                              JSONArray messageArray = actionsObject.getJSONArray("inApp");
                              appLaunchCacheManager.addString(INAPP_MESSAGES,messageArray.toString());
                              processInAppMessages(messageArray);
                              AppLaunchResponse actionsResponse  = new AppLaunchResponse();
                              actionsResponse.setResponseJSON(actionsObject);
                              appLaunchActions.onSuccess(actionsResponse);
                            //  appLaunchActions.onFeaturesReceived(featuresArray.toString());
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                      }
                  }

                  @Override
                  public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                      if(response!=null){
                          Log.d("getActions",response.getResponseText());
                          appLaunchActions.onFailure(new AppLaunchFailResponse(ErrorCode.FETCH_ACTIONS_FAILURE,response.getResponseText()));
                      }else{
                          appLaunchActions.onFailure(new AppLaunchFailResponse(ErrorCode.FETCH_ACTIONS_FAILURE,"Error fetching actions"));
                      }

                  }
              });
          }
          }
      }


    /**
     * Checks to see if the feature code specified is present
     * returns true if the feature is present else false
     * @param featureCode
     * @return
     * @throws AppLaunchException
     */
    public boolean isFeatureEnabled(String featureCode) throws AppLaunchException{
        if(null!=appLaunchCacheManager ){
            if(appLaunchCacheManager.getBoolean(AppLaunchConstants.ACTIONS_INVOKED,false)){
                if (!featureList.isEmpty() && featureList.containsKey(featureCode)) {
                    return true;
                }
                return false;
            }
            throw new AppLaunchException("Invoke init() api before isFeatureEnabled()");
        }
        throw new AppLaunchException("init() api should be invoked as the first call in the application.");
    }

    /**
     * This api returns the variable for the feature code
     * @param featureCode
     * @param propertyCode
     * @return
     */
    public String getPropertyOfFeature(String featureCode, String propertyCode) throws AppLaunchException {
        if(null!=appLaunchCacheManager ){
            if(appLaunchCacheManager.getBoolean(AppLaunchConstants.ACTIONS_INVOKED,false)) {
                String returnValue = null;
                if (featureList.containsKey(featureCode)) {
                    JSONObject featureObject = featureList.get(featureCode);
                    try {
                        JSONArray variableArray = featureObject.getJSONArray(PROPERTIES);
                        for (int index = 0; index < variableArray.length(); index++) {
                            try {
                                JSONObject variableObject = (JSONObject) variableArray.get(index);
                                if (variableObject.getString(CODE).equals(propertyCode)) {
                                    returnValue = variableObject.getString(VALUES);
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
            }
            else{
                throw new AppLaunchException("Invoke init() api before getPropertyOfFeature()");
            }
        }
        throw new AppLaunchException("init() api should be invoked as the first call in the application.");
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


    private void register(final AppLaunchListener appLaunchListener, Hashtable parameters,boolean updateUser) throws JSONException, Exception{
        if (AppLaunchUtils.validateString(appLaunchConfig.getClientSecret()) && AppLaunchUtils.validateString(appLaunchConfig.getApplicationId()) && AppLaunchUtils.validateString(appLaunchConfig.getUserID())) {
            AppLaunchAnalytics.init(appContext, appLaunchConfig.getUserID(), "", appLaunchConfig.getClientSecret(), true, Analytics.DeviceEvent.ALL);
            this.clientSecret = appLaunchConfig.getClientSecret();
            this.appGUID = appLaunchConfig.getApplicationId();
            //send all the analytics event to the server
            sendLogs();
            //add listener to track app events
            JSONObject initJson = AppLaunchUtils.getInitJson(appContext);
            if (initJson == null) {
                throw new RuntimeException("Error constructing registerDevice json body");
            }
            initJson.put("userId", appLaunchConfig.getUserID());
            initJson.put("deviceId", appLaunchConfig.getDeviceId());
            if(parameters!=null && parameters.size()>0){
                JSONObject paramsJson = new JSONObject();
                Enumeration keys = parameters.keys();
                while(keys.hasMoreElements()){
                    String key = (String) keys.nextElement();
                   // initJson.put(key,parameters.get(key));
                    paramsJson.put(key,parameters.get(key));
                }
                initJson.put("attributes",paramsJson);
                appLaunchCacheManager.addString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId()+"-params",paramsJson.toString());
            }
            //construct registration url
            String registrationUrl = appLaunchUrlBuilder.getAppRegistrationURL();
            //post the body to the server
            AppLaunchInternalListener appLaunchInternalListener = new AppLaunchInternalListener() {
                @Override
                public void onSuccess(AppLaunchResponse appLaunchResponse) {
                    loadActions(appLaunchListener);
                }

                @Override
                public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                    appLaunchListener.onFailure(new AppLaunchFailResponse(ErrorCode.REGISTRATION_FAILURE,appLaunchFailResponse.getErrorMsg()));
                }
            };
            if(updateUser){
                sendPutRequest("initialize", registrationUrl, initJson, appLaunchInternalListener);
            }else{
                sendPostRequest("initialize", registrationUrl, initJson, appLaunchInternalListener);
            }

        } else {
            logger.error("AppLaunchCore:initialize() - An error occured while initializing AppLaunchCore service. Add a valid ClientSecret and AppSecret Value");
            System.out.print("AppLaunchCore:initialize() - An error occured while initializing AppLaunchCore service. Add a valid ClientSecret and AppSecret Value");
            throw new Exception("AppLaunchCore:initialize() - An error occured while initializing AppLaunchCore service. Add a valid ClientSecret and AppSecret Value", null);
        }
    }


    /**
     * The metric to be sent to the server
     *
     * @param metrics
     */
    public void sendMetrics(ArrayList<String> metrics) {
       // String metricsUrl = ANALYZER_URL+"/users/"+ appLaunchConfig.getUserID()+ "/events/metrics?deviceId="+ AppLaunchUtils.getDeviceId();
        String metricsUrl = appLaunchUrlBuilder.getMetricsURL();
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
            sendPostRequest("SendMetrics", metricsUrl, metricJson, new AppLaunchInternalListener() {
                @Override
                public void onSuccess(AppLaunchResponse appLaunchResponse) {

                //    Log.d("sendMetricsSuccess", appLaunchResponse.getResponseJSON().toString());
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
                    appLaunchCacheManager.addFeatureToCache(jsonObject);
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
            if (null != appLaunchConfig && null !=appContext) {
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
                        //    AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                         //   appLaunchResponseListener.onFailure(appLaunchFailResponse);
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

            if (null != appLaunchConfig && null != appContext && null != featureCode && null!= appLaunchResponseListener) {
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
                           // appLaunchResponseListener.onFailure(new AppLaunchFailResponse());
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != appLaunchResponseListener) {
                          //  AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                          //  appLaunchResponseListener.onFailure(appLaunchFailResponse);
                        }
                    }
                });
            }

        } catch (Exception ex) {
            logger.error("EngageCore:isFeatureEnabled() - An error occured fetching features.");
        }

    }


    private String getDeviceId() {
        return deviceId;
    }

    private String getUserLocale() {
        return userLocale;
    }



    private void sendPostRequest(final String methodName, String url, JSONObject body, final AppLaunchInternalListener appLaunchListener) {

        Request postReq = new Request(url, Request.POST);
       // postReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

        Map<String, List<String>> headers = new HashMap<>();
        List<String> headerValues = new ArrayList<>();
        headerValues.add("application/json");
       // headerValues.add("application/json; charset = UTF-8");
        headers.put("content-type", headerValues);
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
                if("initialize".equals(methodName) && appLaunchCacheManager!=null){
                    appLaunchCacheManager.addString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),response.getResponseText());
                    /*SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),response.getResponseText());
                    editor.commit();*/
                }
                appLaunchListener.onSuccess(appLaunchResponse);
                //  responseListener.onSuccess("SUCCESS:: invoke function pushed");
                try {
                    appLaunchResponse.setResponseJSON(new JSONObject(response.getResponseText()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.INTERNAL_ERROR,response.getResponseText());
                appLaunchListener.onFailure(appLaunchFailResponse);
            }
        });
    }

    private void sendPutRequest(final String methodName, String url, JSONObject body, final AppLaunchInternalListener appLaunchListener) {
        Request putReq = new Request(url, Request.PUT);
        // putReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

        Map<String, List<String>> headers = new HashMap<>();
        List<String> headerValues = new ArrayList<>();
        headerValues.add("application/json");
        headers.put("Content-Type", headerValues);
        List<String> secretValues = new ArrayList<>();
        secretValues.add(appLaunchConfig.getClientSecret());
        headers.put("clientSecret", secretValues);
        // headers.put("clientSecret",appLaunchConfig.getClientSecret());
        putReq.setHeaders(headers);

        putReq.send(appContext, body.toString(), new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("POST INVOKE FUNCTION::", response.getResponseText());
                AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                if("initialize".equals(methodName) && appLaunchCacheManager!=null){
                    appLaunchCacheManager.addString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),response.getResponseText());
                    /*SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(appLaunchConfig.getUserID()+"-"+appLaunchConfig.getBluemixRegion()+"-"+appLaunchConfig.getApplicationId(),response.getResponseText());
                    editor.commit();*/
                }
                appLaunchListener.onSuccess(appLaunchResponse);
                //  responseListener.onSuccess("SUCCESS:: invoke function pushed");
                try {
                    appLaunchResponse.setResponseJSON(new JSONObject(response.getResponseText()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.INTERNAL_ERROR,response.getResponseText());
                appLaunchListener.onFailure(appLaunchFailResponse);
            }
        });
    }

    private void sendDeleteRequest(String url, final AppLaunchInternalListener appLaunchListener) {

        Request postReq = new Request(url, Request.DELETE);
        // postReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());

        Map<String, List<String>> headers = new HashMap<>();
        List<String> secretValues = new ArrayList<>();
        secretValues.add(appLaunchConfig.getClientSecret());
        headers.put("clientSecret", secretValues);
        postReq.setHeaders(headers);

        postReq.send(appContext, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("POST INVOKE FUNCTION::", response.getResponseText());
                if(response.getStatus() == 202) {
                    AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                    appLaunchListener.onSuccess(appLaunchResponse);
                } else {
                    AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.UNREGISTRATION_FAILURE,response.getResponseText());
                    appLaunchListener.onFailure(appLaunchFailResponse);
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse(ErrorCode.UNREGISTRATION_FAILURE,response.getResponseText());
                appLaunchListener.onFailure(appLaunchFailResponse);
            }
        });
    }



    /**
     * Get messages for the application
     * @param context
     * @param appLaunchResponseListener
     */
    private void getMessages(final Context context, final AppLaunchResponseListener appLaunchResponseListener){

        String messageUrl = ANALYZER_URL+"/users/"+ appLaunchConfig.getUserID()+"/actions/messages?deviceId="+ AppLaunchUtils.getDeviceId();
        Request getReq = new Request(messageUrl, Request.GET);
        getReq.addHeader("clientSecret",appLaunchConfig.getClientSecret());
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
                   //     JSONObject messageJson = new JSONObject(response.getResponseText());
                        JSONObject messageJson = new JSONObject(jsonResponse);
                        processInAppMessages(context,messageJson);
                    }catch (Exception ex){
                     //   AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                      //  appLaunchFailResponse.setErrorMsg("Error parsing response: " + ex.getMessage());
                        //  engageResponseListener.onFailure(engageFailResponse);
                     //   ex.printStackTrace();
                    }

                }else{
                    AppLaunchResponse appLaunchResponse = new AppLaunchResponse();
                 //   appLaunchResponse.setResponseText(response.getResponseText());
                    appLaunchResponseListener.onSuccess(appLaunchResponse);
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                if(response!=null){
                    Log.d("getMessages",response.getResponseText());
                //    AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
                //    appLaunchFailResponse.setErrorMsg(response.getResponseText());
                //    appLaunchResponseListener.onFailure(appLaunchFailResponse);
                }
            }
        });
    }


    private void processInAppMessages(JSONArray inAppMsgList){
        if(inAppMsgList!=null) {
            try {
                if (null != inAppMsgList && inAppMsgList.length() > 0) {
                    messageList.clear();
                    for (int inappIndex = 0; inappIndex < inAppMsgList.length(); inappIndex++) {
                        JSONObject inappMessage = inAppMsgList.getJSONObject(inappIndex);
                        String layout = inappMessage.getString("layout");
                        if (MessageTypes.BANNER.equals(layout)) {
                            final MessageData messageData = new MessageData(MessageTypes.BANNER);
                            messageData.setTitle(inappMessage.getString("title"));
                            messageData.setName(inappMessage.getString("name"));
                            messageData.setSubTitle(inappMessage.getString("subtitle"));
                            messageData.setImageUrl(inappMessage.getString("imageUrl"));
                            if(inappMessage.has("triggers")){
                                JSONArray triggerList = inappMessage.getJSONArray("triggers");
                                for(int i=0; i<triggerList.length();i++){
                                    JSONObject triggerObject = (JSONObject) triggerList.get(i);
                                    String trigger =(String) triggerObject.get("action");
                                    messageData.addTrigger(trigger);
                                }
                            }
                            //process the buttons
                            processButtons(messageData,inappMessage);
                            messageList.put(messageData.getName(),messageData);
                        } else if (MessageTypes.TOP_SLICE.equals(layout)) {

                        } else if (MessageTypes.BOTTOM_PANEL.equals(layout)) {

                        }
                    }
                }
            } catch (JSONException e) {
              //  AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
              //  appLaunchFailResponse.setErrorMsg("Error parsing response: " + e.getMessage());
                //  engageResponseListener.onFailure(engageFailResponse);
              //  e.printStackTrace();
            }
        }
    }

    public void displayInAppMessages(final Context context){
        if(context!=null){
            ((Activity)context).runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Set<String> messageKeys= messageList.keySet();
                    Iterator keys = messageKeys.iterator();
                    while (keys.hasNext()){
                        String key = (String) keys.next();
                        MessageData messageData = messageList.get(key);
                        ArrayList<String> triggerList = messageData.getTriggerList();
                        Iterator triggerItr = triggerList.iterator();
                        while(triggerItr.hasNext()) {
                            String trigger = (String) triggerItr.next();
                            switch (trigger) {
                                case TRIGGER_EVERY_LAUNCH:
                                    displayBannerDialog(context, messageList.get(key));
                                    break;
                                case TRIGGER_EVERY_ALTERNATE_LAUNCH:
                                   boolean displayed= appLaunchCacheManager.getBoolean(messageData.getName()+trigger,false);
                                    if(!displayed){
                                        appLaunchCacheManager.addBoolean(messageData.getName()+trigger,true);
                                        displayBannerDialog(context, messageList.get(key));
                                    }else{
                                        appLaunchCacheManager.addBoolean(messageData.getName()+trigger,false);
                                    }
                                    break;
                                case TRIGGER_FIRST_LAUNCH:
                                    long date = appLaunchCacheManager.getLong(messageData.getName(),0);
                                    if(date!=0){
                                        Date previousDisplayDate = new Date(date);
                                        Date today = new Date();
                                        try {
                                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                            previousDisplayDate = formatter.parse(formatter.format(previousDisplayDate));
                                            today =  formatter.parse(formatter.format(today));
                                            //if the dialog is not displayed earlier in the day proceed to display
                                            if(previousDisplayDate.compareTo(today)!=0)
                                                displayBannerDialog(context, messageList.get(key));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case TRIGGER_ONCE_AND_ONLY_ONCE:
                                    if(!appLaunchCacheManager.getBoolean(messageData.getName()+"-displayed",false)){
                                        displayBannerDialog(context, messageList.get(key));
                                    }
                                    break;
                            }
                        }
                    }
                }
            });
        }
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
            //    AppLaunchFailResponse appLaunchFailResponse = new AppLaunchFailResponse();
            //    appLaunchFailResponse.setErrorMsg("Error parsing response: " + e.getMessage());
                //  engageResponseListener.onFailure(engageFailResponse);
            //    e.printStackTrace();
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
                    if(buttonObject.has("metrics")){
                        buttonData.setMetrics(buttonObject.getJSONArray("metrics"));
                    }
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
                    if(metricsList!=null){
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
                    }
                    dialog.dismiss();
                }
            });

        }
        dialog.show();
        appLaunchCacheManager.addLong(messageData.getName(),new Date().getTime());
        appLaunchCacheManager.addBoolean(messageData.getName()+"-displayed",true);
    }


   /* public void updateUser(final AppLaunchResponseListener responseListener) {

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
    }*/

  /*  public void putFunctionTrigger(final String functionName, final AppLaunchResponseListener responseListener) throws JSONException {

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
    }*/

    public void showDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater)appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setView(layout);
        adb.show();
    }

    // Setup a recurring alarm every half hour
    private void scheduleAlarm(int timeInterval) {
        if(appContext!=null){
            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(appContext, AppLaunchAlarmReceiver.class);
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(appContext, AppLaunchAlarmReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Setup periodic alarm every every half hour from this point onwards
            long firstMillis = System.currentTimeMillis(); // alarm is set right away
            AlarmManager alarm = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    timeInterval*1000,pIntent);
        }
    }
}
