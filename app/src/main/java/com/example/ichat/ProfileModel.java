package com.example.ichat;

public class ProfileModel {
    String profileUri, username, aboutUser, userNumber, userId,fcmToken,DisplayName;
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProfileModel(String DisplayName,String profileUri, String username, String aboutUser, String userNumber, String userId, String fcmToken) {
        this.DisplayName = DisplayName;
        this.profileUri = profileUri;
        this.username = username;
        this.aboutUser = aboutUser;
        this.userNumber = userNumber;
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public ProfileModel() {
    }
}
