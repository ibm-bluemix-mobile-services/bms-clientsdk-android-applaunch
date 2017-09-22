IBM Bluemix App Launch Android SDK
==========================================

[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=development)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5c49c09a1f9f45c99c39623f8033d1eb)](https://www.codacy.com/app/ibm-bluemix-mobile-services/bms-clientsdk-android-push?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ibm-bluemix-mobile-services/bms-clientsdk-android-push&amp;utm_campaign=Badge_Grade)

The [Bluemix App Launch service](https://console.ng.bluemix.net/catalog/) Engage service on Bluemix helps in controlled reach of app features. It provides a unified service to customize and personalize your applications to different audience with just few clicks.

Ensure that you go through [Bluemix App Launch service documentation](https://console.ng.bluemix.net/docs/services/) before you start.

## Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Initialize SDK](#initialize-sdk)
	- [Include client App Launch SDK](#include-client-app-launch-sdk)
	- [Initialize](#initialize)	
- [Actions](#actions)
- [Feature Toggle](#feature-toggle)
	- [Check if feature is enabled](#check-if-feature-is-enabled)
	- [Get variable for feature](#get-variable-for-feature)
- [Metrics](#metrics)
	- [Send Metrics](#send-metrics)
- [Samples and videos](#samples-and-videos)


## Prerequisites


 * [Engage Android Client SDK package](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.ibm.mobilefirstplatform.clientsdk.android%22)
 * Android API level 14 or later
 * Android 4.0 or later
 * [Android Studio](https://developer.android.com/studio/index.html)
 * [Gradle](https://gradle.org/install)
 * [Android HelloEngage sample app](https://github.ibm.com/Engage/bms-samples-android-helloengage)
 * [BMSCore](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-core) SDKs installed by using 
  either Android Studio or Gradle

## Installation

Choose to integrate the Engage Service Android Client SDK package using either of the following options:

- Download and import the package to your Android Studio project

## Initialize SDK


### Include client App Launch SDK 

Import `engage-client-android.aar` as a module into the project and Configure the app module `build.gradle` files.

1. Add Bluemix App Launch Android SDK dependency and BMS Core dependency to your app module `build.gradle` file.
	
	```
	dependencies {
    	........
		compile project(':engage-client-android')
		 compile 'com.ibm.mobilefirstplatform.clientsdk.android:core:[2.0.0,3.0.0)'
		.......
	}
	```
2. Configure the `AndroidManifest.xml` file. Refer the [example here](https://github.ibm.com/Engage/bms-samples-android-helloengage/blob/master/PizzaDelivery/app/src/main/AndroidManifest.xml). Add the following permissions inside application's `AndroidManifest.xml` file. 

	 ```
	 <uses-permission android:name="android.permission.INTERNET"/>
     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	 ```
### Initialize
A common place to put the initialization code is the`onCreate()`method of the `main activity` in your Android application: 

```
// Initialize the SDK
EngageConfig engageConfig = new EngageConfig(getApplication(), "bluemixRegionSuffix","appGUID","clientSecret","user");
        EngageClient.getInstance().register(engageConfig, new EngageResponseListener() {
            @Override
            public void onSuccess(EngageResponse engageResponse) {
                Log.d("MainActivity","Init Successful - "+engageResponse.getResponseText());
            }

            @Override
            public void onFailure(EngageFailResponse engageFailResponse) {
                Log.d("MainActivity","Init Failed - "+engageFailResponse.getErrorMsg());
            }
        });
```

Where `bluemixRegionSuffix` specifies the location where the app is hosted. You can use any of the following values:

- `BMSClient.REGION_US_SOUTH`
- `BMSClient.REGION_UK`
- `BMSClient.REGION_SYDNEY`

The `appGUID` is the engage app GUID value, while `clientSecret` is the engage client secret value which can be obtained from the service console.

**Note: register should be the first call in the application.**

## Actions

### Get Actons

Use the ` EngageClient.getInstance().getActions()` API to fetch all the actions assosicated with the application. 

     EngageClient.getInstance().getActions(AppLaunchActions);

Here AppLaunchActions is an interface which has to be implemented in the application. The interface provides callback methods which gets triggered if the features are present in the actions.        


## Feature Toggle

### Check if feature is enabled

Use the ` EngageClient.getInstance().isFeatureEnabled()` API to check if a particular feature is enabled for the application. This api returns true if the feature is enable for the application else false.


     EngageClient.getInstance().isFeatureEnabled(featureCode)
     
        
### Get variable for feature
Use the `EngageClient.getInstance().getVariableForFeature()` to fetch the variable corresponding to a feature

	EngageClient.getInstance().getVariableForFeature("featurecode","variablecode");
	

This api returns the varaible corresponding to the variable code for a particular feature.

## Metrics

### Send Metrics

To send metrics to the server use the `EngageClient.getInstance().sendMetrics();` api. This sends the metrics information to the server

```
 EngageClient.getInstance().sendMetrics("metriccode");
```

## Samples and videos

* For samples, visit - [Github Sample](https:)

* For video tutorials, visit - [Engage Service](https://)

### Learning More

* Visit the **[Bluemix Developers Community](https://developer.ibm.com/bluemix/)**.

* [Getting started with IBM MobileFirst Platform for iOS](https://www.ng.bluemix.net/docs/mobile/index.html)

### Connect with Bluemix

[Twitter](https://twitter.com/ibmbluemix)|
[YouTube](https://www.youtube.com/watch?v=dQ1WcY_Ill4) |
[Blog](https://developer.ibm.com/bluemix/blog/) |
[Facebook](https://www.facebook.com/ibmbluemix) |
[Meetup](http://www.meetup.com/bluemix/)

=======================
Copyright 2016-17 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
