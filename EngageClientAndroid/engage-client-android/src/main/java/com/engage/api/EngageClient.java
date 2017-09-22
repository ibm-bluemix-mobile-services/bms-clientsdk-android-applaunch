package com.engage.api;

import android.app.Activity;
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

import com.engage.EngageFailResponse;
import com.engage.EngageResponse;
import com.engage.EngageResponseListener;
import com.engage.R;
import com.engage.common.EngageConstants;
import com.engage.common.EngageProperties;
import com.engage.common.EngageUtils;
import com.engage.logger.EngageAnalytics;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.engage.R.id.buttonpanel;
import static com.engage.common.EngageConstants.ACTIONS_INVOKED;

/**
 * Created by norton on 7/21/17.
 */

public class EngageClient {


    private EngageProperties engageProperties;
    public static final String PREFS_NAME = "com.ibm.mobile.services.engage";
    // public static final String URL = "http://10.0.2.2:8080/api/v1/";

    private String URL;
    private String ANALYZER_URL;
    private String appGUID;
    private String clientSecret;
    private String deviceId;
    private String userOSType = "android";
    private String userLocale;
    private EngageConfig engageConfig;
    private static final String APP_LAUNCH = "AppLaunch" ;
    //private String userGender;
    //private int userAge;
    //private long userLatitude;
    //private long userLongitude;
    private boolean isInitialized = false;
    private EngageResponseListener myListener;
    SharedPreferences prefs = null;
    private JSONArray features = null;
    private boolean renderUi = true;
    private HashMap<String, JSONObject> featureList;
    private SharedPreferences sharedpreferences;
    private boolean isFirstTimeUser;

    private String actions=null;
    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + EngageClient.class.getSimpleName());

    private static Context appContext = null;

    private static EngageClient thisInstance = null;

    private EngageClient() {
        super();
        featureList = new HashMap<>();
    }

    public synchronized static EngageClient getInstance() {
        if (thisInstance == null) {
            thisInstance = new EngageClient();
        }
        return thisInstance;
    }

    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    public void registerUser(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext() && null != engageResponseListener) {
                sharedpreferences = engageConfig.getApplication().getSharedPreferences(APP_LAUNCH, Context.MODE_PRIVATE);
                isFirstTimeUser = sharedpreferences.getBoolean(EngageConstants.FIRST_TIME_USER,true);
                BMSClient.getInstance().initialize(engageConfig.getContext(), BMSClient.REGION_US_SOUTH);
                this.engageConfig = engageConfig;
                //  EngageActivityLifeCycleCallbackListener.init(engageConfig.getApplication());
                appContext = engageConfig.getContext();
                engageProperties = EngageProperties.getInstance(engageConfig.getContext());
                URL = engageProperties.getProtocol() + "://" + engageProperties.getHost() + ":" + engageProperties.getPort() + engageProperties.getServerContext();
                ANALYZER_URL = engageProperties.getAnalyzerProtocol() + "://" + engageProperties.getAnalyzerServerHost() + ":" + engageProperties.getAnalyzerServerPort() + engageProperties.getAnalyzerServerContext();
                ANALYZER_URL += engageConfig.getApplicationId();
                ANALYZER_URL+="/users/"+engageConfig.getUserID()+"/devices/"+EngageUtils.getDeviceId();
            } else {
                logger.error("EngageCore:initialize() - An error occured while initializing EngageClient service. Invalid context");
                throw new Exception("EngageCore:initialize() - An error occured while initializing EngageClient service. Invalid context", null);
            }
            //proceed to registration only if the user is a first time user
            if(isFirstTimeUser){
                init(engageResponseListener);
            }else{
                String registrationResponse = sharedpreferences.getString(EngageConstants.REG_RESPONSE,"");
                EngageResponse engageResponse = new EngageResponse();
                engageResponse.setResponseText(registrationResponse);
                engageResponseListener.onSuccess(engageResponse);
            }

        } catch (Exception e) {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageFailResponse.setErrorMsg(e.getMessage());
            engageResponseListener.onFailure(engageFailResponse);
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service.");
            throw new RuntimeException(e);
        }
    }


    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    public void updateUser(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null == engageConfig && null == engageConfig.getContext() && null == engageResponseListener) {
                logger.error("EngageCore:updateUser() - An error occured while updateUser in EngageClient service. Invalid context");
                throw new Exception("EngageCore:updateUser() -  An error occured while updateUser in EngageClient service. Invalid context", null);
            }
            init(engageResponseListener);
        } catch (Exception e) {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageFailResponse.setErrorMsg(e.getMessage());
            engageResponseListener.onFailure(engageFailResponse);
            logger.error("EngageCore:updateUser() - An error occured while updateUser EngageCore service.");
            throw new RuntimeException(e);
        }
    }


    /**
     * @param appLaunchActions
     */
    public void getActions(final AppLaunchActions appLaunchActions) {
      if(appLaunchActions!=null){
          if (null != engageConfig && null != engageConfig.getContext()) {
              String actionsUrl = ANALYZER_URL + "/actions";
              Request getReq = new Request(actionsUrl, Request.GET);

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
        if(sharedpreferences.getBoolean(EngageConstants.ACTIONS_INVOKED,false)){
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
    public String getVariableForFeature(String featureCode, String variableCode) throws AppLaunchException {
        if(sharedpreferences.getBoolean(EngageConstants.ACTIONS_INVOKED,false)) {
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
            throw new AppLaunchException("Invoke getActions() api before getVariableForFeature()");
        }
    }

    /**
     * Sends all the analytics event to the server
     */
    private void sendLogs(){
        EngageAnalytics.send(new ResponseListener() {
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


    private void init(EngageResponseListener engageResponseListener) throws JSONException, Exception{
        if (EngageUtils.validateString(engageConfig.getClientSecret()) && EngageUtils.validateString(engageConfig.getApplicationId()) && EngageUtils.validateString(engageConfig.getUserID())) {
            EngageAnalytics.init(engageConfig.getApplication(), engageConfig.getUserID(), "", engageConfig.getClientSecret(), true, Analytics.DeviceEvent.ALL);
            this.clientSecret = engageConfig.getClientSecret();
            this.appGUID = engageConfig.getApplicationId();
//            ANALYZER_URL += engageConfig.getApplicationId();
//            ANALYZER_URL+="/users/"+engageConfig.getUserID()+"/devices/"+EngageUtils.getDeviceId();
            //override the default server url to send analytics information
            EngageAnalytics.overrideServerHost = ANALYZER_URL;
            //send all the analytics event to the server
            sendLogs();
            //add listener to track app events
            JSONObject initJson = EngageUtils.getInitJson();
            if (initJson == null) {
                throw new RuntimeException("Error constructing init json body");
            }
            initJson.put("userId", engageConfig.getUserID());
            //construct initialze url
            String registrationUrl = URL + "apps/" + engageConfig.getApplicationId() + "/users";
            //post the body to the server
            sendPostRequest("initialize", registrationUrl, initJson, engageResponseListener);
        } else {
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value");
            System.out.print("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value");
            throw new Exception("EngageCore:initialize() - An error occured while initializing EngageCore service. Add a valid ClientSecret and AppSecret Value", null);
        }
    }


    /**
     * The metric to be sent to the server
     *
     * @param metric
     */
    public void sendMetrics(ArrayList<String> metric) {
        String metricsUrl = ANALYZER_URL + "/events/metrics";
        try {
            JSONObject metricJson = EngageUtils.getMetricJson();
            if (metricJson == null) {
                throw new RuntimeException("Error creating Metrics payload");
            }
            metricJson.put("userId", engageConfig.getUserID());
            metricJson.put("metricCodes", metric.toString());
            sendPostRequest("SendMetrics", metricsUrl, metricJson, new EngageResponseListener() {
                @Override
                public void onSuccess(EngageResponse engageResponse) {
                    Log.d("sendMetricsSuccess",engageResponse.getResponseText());
                }

                @Override
                public void onFailure(EngageFailResponse engageFailResponse) {
                    Log.d("sendMetricsFailure",engageFailResponse.getErrorMsg());
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
     * @param engageResponseListener
     */
    private void getFeatures(final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext()) {
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
                        if(null!=engageResponseListener){
                            EngageResponse engageResponse = new EngageResponse();
                            engageResponseListener.onSuccess(engageResponse);
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != engageResponseListener) {
                            EngageFailResponse engageFailResponse = new EngageFailResponse();
                            engageResponseListener.onFailure(engageFailResponse);
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
    private void isFeatureEnabled(final String featureCode,final EngageResponseListener engageResponseListener) {
        try {

            if (null != engageConfig && null != engageConfig.getContext() && null != featureCode && null!=engageResponseListener) {
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
                            engageResponseListener.onSuccess(new EngageResponse());

                        } else {
                            engageResponseListener.onFailure(new EngageFailResponse());
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != engageResponseListener) {
                            EngageFailResponse engageFailResponse = new EngageFailResponse();
                            engageResponseListener.onFailure(engageFailResponse);
                        }
                    }
                });
            }

        } catch (Exception ex) {
            logger.error("EngageCore:isFeatureEnabled() - An error occured fetching features.");
        }

    }




    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    private void getThemes(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext() && null != engageResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
                    }
                });

            }
        } catch (Exception ex) {

        }
    }


    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    private void getCustomizations(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext() && null != engageResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
                    }
                });

            }
        } catch (Exception ex) {

        }
    }

    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    private void getMessages(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext() && null != engageResponseListener) {
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
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
     * @param engageResponseListener
     */
    private void updateUser(String key, String value, final EngageResponseListener engageResponseListener) {
        if (null != key && null != value && null != engageResponseListener) {
            try {
                JSONObject jsonObject = EngageUtils.getInitJson();
                jsonObject.put(key, value);

                Request getReq = new Request(URL + " /apps/" + "{applicationid}" + "/users/" + "{userid}", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
                    }
                });
            } catch (JSONException e) {
                EngageFailResponse engageFailResponse = new EngageFailResponse();
                engageResponseListener.onFailure(engageFailResponse);
                e.printStackTrace();
            }
        } else {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageResponseListener.onFailure(engageFailResponse);
        }

    }

    /**
     * @param key
     * @param value
     * @param engageResponseListener
     */
    private void updateUser(String key, int value, final EngageResponseListener engageResponseListener) {
        if (null != key && null != engageResponseListener) {
            try {
                JSONObject jsonObject = EngageUtils.getInitJson();
                jsonObject.put(key, value);
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
                    }
                });
            } catch (JSONException e) {
                EngageFailResponse engageFailResponse = new EngageFailResponse();
                engageResponseListener.onFailure(engageFailResponse);
                e.printStackTrace();
            }
        } else {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageResponseListener.onFailure(engageFailResponse);
        }

    }


    /**
     * @param key
     * @param value
     * @param engageResponseListener
     */
    private void updateUser(String key, boolean value, final EngageResponseListener engageResponseListener) {
        if (null != key && null != engageResponseListener) {
            try {
                JSONObject jsonObject = EngageUtils.getInitJson();
                jsonObject.put(key, value);
                Request getReq = new Request(URL + "captivateengine/features", Request.GET);

                getReq.send(appContext, new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        EngageResponse engageResponse = new EngageResponse();
                        engageResponseListener.onSuccess(engageResponse);
                    }

                    @Override
                    public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageResponseListener.onFailure(engageFailResponse);
                    }
                });
            } catch (JSONException e) {
                EngageFailResponse engageFailResponse = new EngageFailResponse();
                engageResponseListener.onFailure(engageFailResponse);
                e.printStackTrace();
            }
        } else {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageResponseListener.onFailure(engageFailResponse);
        }

    }


    private void sendPostRequest(final String methodName, String url, JSONObject body, final EngageResponseListener engageResponseListener) {

        Request postReq = new Request(url, Request.POST);

        Map<String, List<String>> headers = new HashMap<>();
        List<String> headerValues = new ArrayList<>();
        headerValues.add("application/json");
        headers.put("Content-Type", headerValues);
        postReq.setHeaders(headers);

        postReq.send(appContext, body.toString(), new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                Log.d("POST INVOKE FUNCTION::", response.getResponseText());
                EngageResponse engageResponse = new EngageResponse();
                engageResponse.setResponseText(response.getResponseText());
                if("initialize".equals(methodName) && sharedpreferences!=null){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(EngageConstants.FIRST_TIME_USER,false);
                    editor.putString(EngageConstants.REG_RESPONSE,response.getResponseText());
                    editor.commit();
                }
                engageResponseListener.onSuccess(engageResponse);
                //  responseListener.onSuccess("SUCCESS:: invoke function pushed");
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                EngageFailResponse engageFailResponse = new EngageFailResponse();
                StringBuilder errorMessage = new StringBuilder();
                if(response!=null)
                    errorMessage.append(methodName + " :" + response.getResponseText());
                if(t!=null){
                    errorMessage.append(":").append(t.getMessage());
                }
                if(extendedInfo!=null){
                    errorMessage.append(extendedInfo.toString());
                }
                engageFailResponse.setErrorMsg(errorMessage.toString());
                engageResponseListener.onFailure(engageFailResponse);
                Log.d("POST INVOKE FUNCTION:: ", methodName + " " + errorMessage.toString());
                Log.d("Extended info:::", errorMessage.toString());
                //  responseListener.onFailure("FAILURE:: invoke function push failed");
            }
        });
    }

    // ********* //


    public void getFeatureToggle(final EngageResponseListener responseListener) {

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

    public void getInAppMsgs(boolean autoRenderUI, final EngageResponseListener responseListener) {

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

    public void getAppTheme(final EngageResponseListener responseListener) {

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

    public void getMessages(final Context context, final EngageResponseListener engageResponseListener){

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
                        EngageFailResponse engageFailResponse = new EngageFailResponse();
                        engageFailResponse.setErrorMsg("Error parsing response: " + ex.getMessage());
                        //  engageResponseListener.onFailure(engageFailResponse);
                        ex.printStackTrace();
                    }

                }else{
                    EngageResponse engageResponse = new EngageResponse();
                    engageResponse.setResponseText(response.getResponseText());
                    engageResponseListener.onSuccess(engageResponse);
                }
            }

            @Override
            public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                if(response!=null){
                    Log.d("getMessages",response.getResponseText());
                    EngageFailResponse engageFailResponse = new EngageFailResponse();
                    engageFailResponse.setErrorMsg(response.getResponseText());
                    engageResponseListener.onFailure(engageFailResponse);
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
                EngageFailResponse engageFailResponse = new EngageFailResponse();
                engageFailResponse.setErrorMsg("Error parsing response: " + e.getMessage());
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



    public void getJsonConfig(final EngageResponseListener responseListener) {

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

    public void updateUser(final EngageResponseListener responseListener) {

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

    public void putFunctionTrigger(final String functionName, final EngageResponseListener responseListener) throws JSONException {

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
