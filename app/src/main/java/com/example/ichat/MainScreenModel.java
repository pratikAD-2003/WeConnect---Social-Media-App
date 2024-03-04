package com.example.ichat;

public class MainScreenModel {
    String uri;
    String username;
    String userNumber;
    String aboutUser;
    String userUid;
    String OnScreenID;
    String fcm_token;

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    String displayName;
    public MainScreenModel() {
    }

    public String getOnScreenID() {
        return OnScreenID;
    }

    public void setOnScreenID(String onScreenID) {
        OnScreenID = onScreenID;
    }


    public MainScreenModel(String uri, String username, String aboutUser, String userUid, String onScreenID, String fcm_token, String displayName) {
        this.uri = uri;
        this.username = username;
        this.aboutUser = aboutUser;
        this.userUid = userUid;
        OnScreenID = onScreenID;
        this.fcm_token = fcm_token;
        this.displayName = displayName;
    }

    public MainScreenModel(String uri, String username, String userNumber, String aboutUser, String userUid, String onScreenID, String displayName, String fcm_token) {
        this.uri = uri;
        this.username = username;
        this.userNumber = userNumber;
        this.aboutUser = aboutUser;
        this.userUid = userUid;
        this.OnScreenID = onScreenID;
        this.displayName = displayName;
        this.fcm_token = fcm_token;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
