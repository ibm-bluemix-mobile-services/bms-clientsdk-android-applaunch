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

public enum RefreshPolicy {

    REFRESH_ON_EVERY_START(0),

    REFRESH_ON_EXPIRY(1),

    BACKGROUND_REFRESH(2);

    private int refresh_policy;

    RefreshPolicy(int  refresh_policy) {
        this.refresh_policy = refresh_policy;
    }

    public int getRefreshPolicy() {
        return refresh_policy;
    }
}
