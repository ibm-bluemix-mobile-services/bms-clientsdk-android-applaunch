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

import org.json.JSONArray;

/**
 * Created by norton on 9/18/17.
 */

public class ButtonData {

    private String buttonName;
    private String action;
    private JSONArray metrics;

    public ButtonData() {
    }

    protected String getButtonName() {
        return buttonName;
    }

    protected void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    protected JSONArray getMetrics() {
        return metrics;
    }

    protected void setMetrics(JSONArray metrics) {
        this.metrics = metrics;
    }

    protected String getAction() {
        return action;
    }

    protected void setAction(String action) {
        this.action = action;
    }
}
