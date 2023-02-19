package com.pd.chatapp.model;

public class CallList {
    private String callId;
    private String sender;
    private String receiver;
    private String userName;
    private String dateTime;
    private String urlProfile;
    private String callType;

    public CallList() {
    }

    public CallList(String callId, String sender, String receiver, String userName, String dateTime, String urlProfile, String callType) {
        this.callId = callId;
        this.sender = sender;
        this.receiver = receiver;
        this.userName = userName;
        this.dateTime = dateTime;
        this.urlProfile = urlProfile;
        this.callType = callType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
