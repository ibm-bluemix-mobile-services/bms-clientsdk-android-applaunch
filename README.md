IBM Bluemix App Launch Android SDK
==========================================
[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch)
[![JitPack](https://img.shields.io/jitpack/v/jitpack/maven-simple.svg)](https://jitpack.io/#ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch)


The [Bluemix App Launch service](https://console.stage1.bluemix.net/catalog/services/app-launch) App Launch service on Bluemix helps in controlled reach of app features. It provides a unified service to customize and personalize your applications to different audience with just few clicks.

Ensure that you go through [Bluemix App Launch service documentation](https://console-regional.ng.bluemix.net/docs/services/app-launch/index.html) before you start.

## Build Status

| Master | Development |
|:------:|:-----------:|
|  [![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch)      |    [![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch.svg?branch=development)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch)         |

## Contents
- [Setup App Launch Service](#setup-app-launch-service)
	 - [Creating the service](#creating-the-service)
	 - [Creating a feature](#creating-a-feature)
	 - [Creating an audience](#creating-an-audience)
	 - [Creating an engagement](#creating-an-engagement)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Initialize SDK](#initialize-sdk)
    - [Include client App Launch SDK](#include-client-app-launch-sdk)
    - [Initialize](#initialize) 
- [Feature Toggle](#feature-toggle)
    - [Check if feature is enabled](#check-if-feature-is-enabled)
    - [Get variable for feature](#get-variable-for-feature)
- [Metrics](#metrics)
    - [Send Metrics](#send-metrics)
- [Samples and videos](#samples-and-videos)

##Setup App Launch Service
### Creating the service
![Create feature](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch/blob/development/Images/create_service.gif)
### Creating a feature
![Create feature](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch/blob/development/Images/create_feature.gif)
### Creating an audience
![Create audience](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch/blob/development/Images/create_audience.gif)
### Creating an engagement
![Create engagement](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-applaunch/blob/development/Images/create_engagement.gif)

## Prerequisites

 * Android API level 14 or later
 * Android 4.0 or later
 * [Android Studio](https://developer.android.com/studio/index.html)
 * [Gradle](https://gradle.org/install)
 * [Android HelloAppLaunch sample app](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-helloapplaunch)
 * [BMSCore](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-core) SDKs installed by using 
  either Android Studio or Gradle

## Installation
## Initialize SDK

### Include client App Launch SDK 

To use the Bluemix App Launch SDK include the following:

1. In the projects `build.gradle` file include:
	```
	allprojects {
	    repositories {
		jcenter()
		maven { url 'https://jitpack.io' }
	    }
	}
	```

2. Add Bluemix App Launch Android SDK dependency and BMS Core dependency to your app module `build.gradle` file.
    
    ```
    dependencies {
        ........
        compile 'com.github.ibm-bluemix-mobile-services:bms-clientsdk-android-applaunch:0.0.1'
        compile 'com.ibm.mobilefirstplatform.clientsdk.android:core:[2.0.0,3.0.0)'
        .......
    }
    ```
3. Configure the `AndroidManifest.xml` file. Refer the [example here](https://github.ibm.com/Engage/bms-samples-android-helloengage/blob/master/PizzaDelivery/app/src/main/AndroidManifest.xml). Add the following permissions inside application's `AndroidManifest.xml` file. 

     ```
     <uses-permission android:name="android.permission.INTERNET"/>
     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     ```
     
### Initialize
A common place to put the initialization code is the`onCreate()`method of the `main activity` in your Android application: 

```
// Initialize the SDK
AppLaunchConfig appLaunchConfig = new AppLaunchConfig.Builder().eventFlushInterval(10).cacheExpiration(10).fetchPolicy(RefreshPolicy.REFRESH_ON_EVERY_START).deviceId("f88ky8u").build();
AppLaunchUser appLaunchUser = new AppLaunchUser.Builder().userId("norton").custom("test","newtest").build();
AppLaunch.getInstance().init(getApplication(), "bluemixRegionSuffix","appGUID","clientSecret",appLaunchConfig,appLaunchUser,AppLaunchListener);
```

The AppLaunchConfig builder is used to customize the following:
`eventFlushInterval` : Decides the time interval the events should be sent to the server. The default value is 30 minutes.

`cacheExpiration` : Decides the time interval the actions should be valid for. On expiration time the actions are fetched from the server. This parameter has effect when the `RefreshPolicy` is set to `RefreshPolicy.REFRESH_ON_EXPIRY` or `RefreshPolicy.BACKGROUND_REFRESH`

`fetchPolicy` : This parameter decides on how frequently the actions should be fetched from the server. The values can be one of the following:

 `RefreshPolicy.REFRESH_ON_EVERY_START`
  
  `RefreshPolicy.REFRESH_ON_EXPIRY`
 
  `RefreshPolicy.BACKGROUND_REFRESH`
 
 `deviceId` : This parameter must be unique.
 
  **Note:Do not rely on the default implementation of the device ID it is not guarenteed to be unique.**

The AppLaunchUser builder is used to provide the following information:

`userId`: The user to be registered

`custom`: This can be used to pass any optional custom attributes. 

Where `bluemixRegionSuffix` specifies the location where the app is hosted. You can use any of the following values:

- `ICRegion.US_SOUTH_STAGING`
- `ICRegion.US_SOUTH`

The `appGUID` is the app launch app GUID value, while `clientSecret` is the appLaunch client secret value which can be obtained from the service console.

**Note: initApp should be the first call in the application.**
     
## Feature Toggle

### Check if feature is enabled

Use the ` AppLaunch.getInstance().isFeatureEnabled()` API to check if a particular feature is enabled for the application. This api returns true if the feature is enable for the application else false.


     AppLaunch.getInstance().isFeatureEnabled(featureCode)
 
 **Note:Throws AppLaunchException if isFeatureEnabled is invoked before getActions() api.**    
        
### Get variable for feature
Use the `AppLaunch.getInstance().getVariableForFeature()` to fetch the variable corresponding to a feature

`AppLaunch.getInstance().getVariableForFeature("featurecode","variablecode");`
`AppLaunch.getInstance().getStringVariableForFeature("featurecode","variablecode");`
`AppLaunch.getInstance().getIntVariableForFeature("featurecode","variablecode");`
`AppLaunch.getInstance().getBooleanVariableForFeature("featurecode","variablecode");`
    

This api returns the varaible corresponding to the variable code for a particular feature.

 **Note:Throws AppLaunchException if getVariableForFeature is invoked before getActions() api.** 

## Metrics

### Send Metrics

To send metrics to the server use the `AppLaunch.getInstance().sendMetrics();` api. This sends the metrics information to the server

```
 AppLaunch.getInstance().sendMetrics("metriccode");
```

## Samples and videos

* For samples, visit - [Github Sample](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-helloapplaunch)


### Learning More

* Visit the **[Bluemix Developers Community](https://developer.ibm.com/bluemix/)**.

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
