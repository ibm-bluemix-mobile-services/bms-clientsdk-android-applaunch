package com.applaunch.api;

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
