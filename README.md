IBM Bluemix App Launch Android SDK
==========================================

The [Bluemix App Launch service](https://console.stage1.bluemix.net/catalog/services/app-launch) App Launch service on Bluemix helps in controlled reach of app features. It provides a unified service to customize and personalize your applications to different audience with just few clicks.

Ensure that you go through [Bluemix App Launch service documentation](https://console-regional.ng.bluemix.net/docs/services/app-launch/index.html) before you start.

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
    - [App Launch Config](#config-app-launch-parameters)
    - [Initialize](#initialize) 
    - [Register](#register) 
    - [Update User](#update-user)   
- [Actions](#actions)
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
### App Launch Config
To configure the applications fetch behaviour include the following parameters in applaunchconfig.properties file.
```
applaunchttl=Provide the time to live for the features in seconds
fetchpolicybackground=true if the feature fetch should be performed in the background else false
```
**Note: This file should be place in the assets folder of the Andriod application.**    

If `fetchpolicybackground` is set to true include the following lines in your applications `AndroidManifest.xml` to allow the sdk to fetch features in the background based on the `applaunchttl` time period provided.

```
<receiver
    android:name="com.ibm.mobile.applaunch.android.actions.ActionsFetchAlarmReceiver"
    android:process=":remote" >
</receiver>
```

```
<service
    android:name="com.ibm.mobile.applaunch.android.actions.ActionsFetchService"
    android:exported="false" /> 
```
     
### Initialize
A common place to put the initialization code is the`onCreate()`method of the `main activity` in your Android application: 

```
// Initialize the SDK
  AppLaunch.getInstance().initApp(getApplication(), "bluemixRegionSuffix","appGUID","clientSecret");
```

Where `bluemixRegionSuffix` specifies the location where the app is hosted. You can use any of the following values:

- `BMSClient.REGION_US_SOUTH`
- `BMSClient.REGION_UK`
- `BMSClient.REGION_SYDNEY`

The `appGUID` is the app launch app GUID value, while `clientSecret` is the appLaunch client secret value which can be obtained from the service console.

**Note: initApp should be the first call in the application.**

### Register
To register the user invoke ```AppLaunch.getInstance().registerUser()``` api: 

```
// Register the user
        AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
        appLaunchParameters.put("customerType","platinum");
        AppLaunch.getInstance().registerUser("userId", appLaunchParameters,new AppLaunchResponseListener() {
            @Override
            public void onSuccess(AppLaunchResponse appLaunchResponse) {
               
           }

            @Override
            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
          
            }
        });
```

The ```AppLaunchParameters``` can be used to pass any optional custom attributes while registering the user. 

Register users can also be invoked in the following ways:

`registerUser(String userId, final AppLaunchResponseListener appLaunchResponseListener)`

`registerUser(String userId)`

`registerUser(String userId,AppLaunchParameters parameters)`

`registerUser(String userId,String key,String value)`

`registerUser(String userId,String key,String value,AppLaunchResponseListener appLaunchResponseListener)`

`registerUser(String userId,AppLaunchParameters parameters,AppLaunchResponseListener appLaunchResponseListener)`

**Note: To update user details invoke the updateUser() api**

### Update User
Use this api to update the user post user registration

```
 AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
        appLaunchParameters.put("customerType","platinum");
 AppLaunch.getInstance().updateUser(appLaunchParameters, new AppLaunchResponseListener() {
            @Override
            public void onSuccess(AppLaunchResponse appLaunchResponse) {
       
            }

            @Override
            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
   
            }
        });
```

The ```AppLaunchParameters``` can be used to pass any optional custom attributes. 

Update user can also be invoked in the following ways:

`updateUser(String key,String value)`

`updateUser(String key,String value, final AppLaunchResponseListener appLaunchResponseListener)`


## Actions

### Get Actons

Use the ` AppLaunch.getInstance().getActions()` API to fetch all the actions assosicated with the application. 

     AppLaunch.getInstance().getActions(AppLaunchActions);

Here AppLaunchActions is an interface which has to be implemented in the application. The interface provides callback methods which gets triggered if the features are present in the actions.        


## Feature Toggle

### Check if feature is enabled

Use the ` AppLaunch.getInstance().isFeatureEnabled()` API to check if a particular feature is enabled for the application. This api returns true if the feature is enable for the application else false.


     AppLaunch.getInstance().isFeatureEnabled(featureCode)
 
 **Note:Throws AppLaunchException if isFeatureEnabled is invoked before getActions() api.**    
        
### Get variable for feature
Use the `AppLaunch.getInstance().getVariableForFeature()` to fetch the variable corresponding to a feature

    AppLaunch.getInstance().getVariableForFeature("featurecode","variablecode");
    

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
