/*
 *     Copyright 2015 IBM Corp.
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.ibm.mobile.applaunch.android.logger;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkLoggingInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long startTime = System.currentTimeMillis();

        String trackingID = UUID.randomUUID().toString();

        Request.Builder requestWithHeadersBuilder = request.newBuilder()
                .header("x-wl-analytics-tracking-id", trackingID);

        //Add the Analytics API key to all outbound requests, so that Push and MCA can use it to log things with the Analytics service
        if(AppLaunchAnalytics.getClientApiKey() != null && !AppLaunchAnalytics.getClientApiKey().equalsIgnoreCase("")){
            //Remove header in case it exists.
            requestWithHeadersBuilder.removeHeader("x-mfp-analytics-api-key");
            requestWithHeadersBuilder.addHeader("x-mfp-analytics-api-key", AppLaunchAnalytics.getClientApiKey());
        }

        Request requestWithHeaders = requestWithHeadersBuilder.build();

        Response response = chain.proceed(requestWithHeaders);

        if(AppLaunchAnalytics.isRecordingNetworkEvents){
            JSONObject metadata = generateRoundTripRequestAnalyticsMetadata(request, startTime, trackingID, response);

            if(metadata != null){
                AppLaunchAnalytics.log(metadata);
            }
        }

        return response;
    }

    protected JSONObject generateRoundTripRequestAnalyticsMetadata(Request request, long startTime, String trackingID, Response response) throws IOException {
        JSONObject metadata = new JSONObject();

        long endTime = System.currentTimeMillis();

        try {
            metadata.put("$path", request.url().toString());
            metadata.put(AppLaunchAnalytics.CATEGORY, "network");
            metadata.put("$trackingid", trackingID);
            metadata.put("$outboundTimestamp", startTime);
            metadata.put("$inboundTimestamp", endTime);
            metadata.put("$roundTripTime", endTime - startTime);

            RequestBody body = request.body();

            if(body != null){
                metadata.put("$bytesSent", body.contentLength());
            }

            if(response != null){
                metadata.put("$responseCode", response.code());
            }

            if(response != null && response.body() != null && response.body().contentLength() >= 0){
                metadata.put("$bytesReceived", response.body().contentLength());
            }

            return metadata;
        } catch (JSONException e) {
            //Do nothing, since it is just for analytics.
            return null;
        }
    }
}
