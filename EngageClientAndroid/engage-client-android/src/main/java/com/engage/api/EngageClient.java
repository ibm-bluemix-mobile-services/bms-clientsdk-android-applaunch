package com.engage.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.engage.EngageFailResponse;
import com.engage.EngageResponse;
import com.engage.EngageResponseListener;
import com.engage.R;
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
    public void initialize(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
        try {
            if (null != engageConfig && null != engageConfig.getContext() && null != engageResponseListener) {
                BMSClient.getInstance().initialize(engageConfig.getContext(), BMSClient.REGION_US_SOUTH);
                this.engageConfig = engageConfig;
                //  EngageActivityLifeCycleCallbackListener.init(engageConfig.getApplication());
                appContext = engageConfig.getContext();
                engageProperties = EngageProperties.getInstance(engageConfig.getContext());
                URL = engageProperties.getProtocol() + "://" + engageProperties.getHost() + ":" + engageProperties.getPort() + engageProperties.getServerContext();
                ANALYZER_URL = engageProperties.getAnalyzerProtocol() + "://" + engageProperties.getAnalyzerServerHost() + ":" + engageProperties.getAnalyzerServerPort() + engageProperties.getAnalyzerServerContext();
            } else {
                logger.error("EngageCore:initialize() - An error occured while initializing EngageClient service. Invalid context");
                throw new Exception("EngageCore:initialize() - An error occured while initializing EngageClient service. Invalid context", null);
            }

             init(engageResponseListener);
        } catch (Exception e) {
            EngageFailResponse engageFailResponse = new EngageFailResponse();
            engageFailResponse.setErrorMsg(e.getMessage());
            engageResponseListener.onFailure(engageFailResponse);
            logger.error("EngageCore:initialize() - An error occured while initializing EngageCore service.");
            throw new RuntimeException(e);
        }
    }


    /**
     * Sends all the analytics event to the server
     */
    private void sendLogs(){
        EngageAnalytics.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
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
            ANALYZER_URL += engageConfig.getApplicationId();
            ANALYZER_URL+="/users/"+engageConfig.getUserID()+"/devices/"+EngageUtils.getDeviceId();
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
    public void sendMetrics(String metric) {
        String metricsUrl = ANALYZER_URL + "/events/metrics";
        try {
            JSONObject metricJson = EngageUtils.getMetricJson();
            if (metricJson == null) {
                throw new RuntimeException("Error creating Metrics payload");
            }
            metricJson.put("userId", engageConfig.getUserID());
            metricJson.put("metricCode", metric);
            sendPostRequest("SendMetrics", metricsUrl, metricJson, new EngageResponseListener() {
                @Override
                public void onSuccess(EngageResponse engageResponse) {

                }

                @Override
                public void onFailure(EngageFailResponse engageFailResponse) {

                }
            });
        } catch (Exception ex) {
            logger.error("EngageCore:sendMetrics() - An error occured while sending metrics to server.");
        }

    }


    /**
     * @param engageResponseListener
     */
    public void getFeatures(final EngageResponseListener engageResponseListener) {
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
    public void isFeatureEnabled(final String featureCode,final EngageResponseListener engageResponseListener) {
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


    public String getVariableForFeature(String featureCode, String variableCode) {
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
    }


    /**
     * @param engageConfig
     * @param engageResponseListener
     */
    public void getThemes(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
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
    public void getCustomizations(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
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
    public void getMessages(EngageConfig engageConfig, final EngageResponseListener engageResponseListener) {
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


    public String getDeviceId() {
        return deviceId;
    }

    public String getUserLocale() {
        return userLocale;
    }


    /**
     * @param key
     * @param value
     * @param engageResponseListener
     */
    public void updateUser(String key, String value, final EngageResponseListener engageResponseListener) {
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
    public void updateUser(String key, int value, final EngageResponseListener engageResponseListener) {
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
    public void updateUser(String key, boolean value, final EngageResponseListener engageResponseListener) {
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
                                "      \"metrics\": [{\n" +
                                "        \"name\": \"bannermessage\",\n" +
                                "        \"code\": \"0x45454\"\n" +
                                "      }]\n" +
                                "    }]\n" +
                                "  }\n" +
                                "  ],\n" +
                                "  \"permissions\": [],\n" +
                                "  \"carousel\": []\n" +
                                "}";
                        JSONObject messageJson = new JSONObject(jsonResponse);
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
                //  responseListener.onFailure(response.getResponseText());
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
                            "      \"metrics\": [{\n" +
                            "        \"name\": \"bannermessage\",\n" +
                            "        \"code\": \"0x45454\"\n" +
                            "      }]\n" +
                            "    }]\n" +
                            "  }\n" +
                            "  ],\n" +
                            "  \"permissions\": [],\n" +
                            "  \"carousel\": []\n" +
                            "}";
                    JSONObject messageJson = new JSONObject(jsonResponse);
                    processInAppMessages(context,messageJson);
                }catch (Exception ex){
                    Log.e("Error InApp", ex.getMessage());
                }

/*               Log.d("getMessages",response.getResponseText());
                EngageFailResponse engageFailResponse = new EngageFailResponse();
               engageFailResponse.setErrorMsg(response.getResponseText());
                engageResponseListener.onFailure(engageFailResponse);*/

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

    private void displayBannerDialog1(Context context,MessageData messageData){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);



//        if(null!=messageData.getButtonOne()){
//            builder.setPositiveButton(messageData.getButtonOne().getButtonName(), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            });
//        }
//
//        if(null!=messageData.getButtonTwo()){
//           builder.setNegativeButton(messageData.getButtonTwo().getButtonName(), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            });
//        }


        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogLayout = inflater.inflate(R.layout.banner_dialog_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImageView image = (ImageView) dialogLayout.findViewById(R.id.goProDialogImage);
        Picasso.with(context).load(messageData.getImageUrl()).placeholder(R.drawable.placeholder_image).into(image);
        // Picasso.with(context).load("https://goo.gl/bktLai").placeholder(R.mipmap.ic_launcher).into(image);
        TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
        titleView.setText(messageData.getTitle());
        TextView subTitleView = (TextView) dialogLayout.findViewById(R.id.subtitle);
        subTitleView.setText(messageData.getSubTitle());

        //add buttons to the banner
     //   LinearLayout buttonPanel = (LinearLayout) dialogLayout.findViewById(R.id.buttonPanel);
     //   ArrayList<ButtonData> buttonDataList = messageData.getButtonDataList();
    /*    for(ButtonData buttonData : buttonDataList) {
            android.widget.Button dialogButton = new android.widget.Button(context);
            buttonPanel.addView(dialogButton);
            dialogButton.setTag(buttonData);
            dialogButton.setText(buttonData.getButtonName());
//            dialogButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ButtonData buttonDataTag = (ButtonData) v.getTag();
//
//                }
//            });

        }*/
        dialog.show();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface d) {
////                ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
////                Bitmap icon=null;
////                try {
////            //        icon = new GetBitmapImage().execute("https://goo.gl/bktLai").get();
////                    if(icon!=null){
//////                        Bitmap icon = BitmapFactory.decodeResource(HomeActivity.this.getResources(),
//////                                R.drawable.android_oreo);
////                        float imageWidthInPX = (float)image.getWidth();
////
////                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
////                                Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
////                        image.setLayoutParams(layoutParams);
////                    }
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                } catch (ExecutionException e) {
////                    e.printStackTrace();
////                }
//
//
////                Bitmap icon = BitmapFactory.decodeResource(HomeActivity.this.getResources(),
////                        R.drawable.android_oreo);
////                float imageWidthInPX = (float)image.getWidth();
////
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
////                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
////                image.setLayoutParams(layoutParams);
//
//
//            }
//        });
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
        dialog.show();
 /*       LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogLayout = inflater.inflate(R.layout.banner_dialog_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImageView image = (ImageView) dialogLayout.findViewById(R.id.goProDialogImage);
        Picasso.with(context).load(messageData.getImageUrl()).placeholder(R.drawable.placeholder_image).into(image);
        // Picasso.with(context).load("https://goo.gl/bktLai").placeholder(R.mipmap.ic_launcher).into(image);
        TextView titleView = (TextView) dialogLayout.findViewById(R.id.title);
        titleView.setText(messageData.getTitle());
        TextView subTitleView = (TextView) dialogLayout.findViewById(R.id.subtitle);
        subTitleView.setText(messageData.getSubTitle());
        dialog.show();*/

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
