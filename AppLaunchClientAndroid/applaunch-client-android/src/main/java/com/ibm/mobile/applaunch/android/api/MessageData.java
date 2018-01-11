package com.ibm.mobile.applaunch.android.api;

import java.util.ArrayList;

/**
 * Created by norton on 9/17/17.
 */

public class MessageData {
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
