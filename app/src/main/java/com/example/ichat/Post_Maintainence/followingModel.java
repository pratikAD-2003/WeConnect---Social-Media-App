package com.example.ichat.Post_Maintainence;

public class followingModel {
    String username,aboutUser,userNumber,userProfile,userUid,userFcm,displayName;

    public followingModel() {
    }

    public followingModel(String username, String aboutUser, String userProfile, String userUid, String userFcm, String displayName) {
        this.username = username;
        this.aboutUser = aboutUser;
        this.userProfile = userProfile;
        this.userUid = userUid;
        this.userFcm = userFcm;
        this.displayName = displayName;
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

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserFcm() {
        return userFcm;
    }

    public void setUserFcm(String userFcm) {
        this.userFcm = userFcm;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
