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

import java.util.ArrayList;

/**
 * Created by norton on 9/17/17.
 */

 class MessageData {
    private String imageUrl;
    private String title;
    private String subTitle;
    private ArrayList<ButtonData> buttonDataList;
    private String metric;
    private String messageType;
    private String name;
    private ArrayList<String> triggerList;

    public MessageData(String messageTypes) {
        this.messageType = messageTypes;
        buttonDataList = new ArrayList<>();
        triggerList = new ArrayList<>();
    }

    protected String getImageUrl() {
        return imageUrl;
    }

    protected void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected String getTitle() {
        return title;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected String getSubTitle() {
        return subTitle;
    }

    protected void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    protected ArrayList<ButtonData> getButtonDataList() {
        return buttonDataList;
    }

    protected void addButton(ButtonData buttonData) {
        this.buttonDataList.add(buttonData);
    }

    protected void addTrigger(String trigger){this.triggerList.add(trigger);}

    public ArrayList<String> getTriggerList() {
        return triggerList;
    }
}
