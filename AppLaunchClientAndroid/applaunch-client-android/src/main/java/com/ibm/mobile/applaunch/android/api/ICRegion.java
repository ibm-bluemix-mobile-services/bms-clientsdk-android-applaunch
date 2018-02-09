/*
 *     Copyright 2018 IBM Corp.
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.ibm.mobile.applaunch.android.api;

/**
 * Created by norton on 1/2/18.
 */


/**
 *  ICRegion is an enumerator which can be used to specify IBM Cloud region where AppLaunch Service is hosted.
 */
public enum ICRegion {


    US_SOUTH(".ng.bluemix.net"),

    UNITED_KINGDOM(".eu-gb.bluemix.net"),

    SYDNEY(".au-syd.bluemix.net"),

    US_SOUTH_STAGING(".stage1.ng.bluemix.net"),

    UNITED_KINGDOM_STAGING(".stage1.eu-gb.bluemix.net"),

    SYDNEY_STAGING(".stage1.au-syd.mybluemix.net"),

    US_SOUTH_DEV(".dev.ng.mybluemix.net"),

    UNITED_KINGDOM_DEV(".dev.eu-gb.bluemix.net"),

    SYDNEY_DEV(".dev.au-syd.bluemix.net");


    private String region;


    ICRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region;
    }
}
