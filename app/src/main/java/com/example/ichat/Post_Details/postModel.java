package com.example.ichat.Post_Details;

public class postModel {

    public static final int IMAGE_POST  = 0;
    public static final int VIDEO_POST  = 1;
    String caption,audioUri,postTime;
    int type;
    String uid;

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    String username,userPic,postKey,fcmToken,aboutUser,displayName;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public postModel(String caption, String audioUri, String postTime, String username, String userPic,String uid,String postKey,int type,String displayName,String fcmToken,String aboutUser) {
        this.caption = caption;
        this.audioUri = audioUri;
        this.postTime = postTime;
        this.username = username;
        this.userPic = userPic;
        this.uid = uid;
        this.type = type;
        this.postKey = postKey;
        this.displayName = displayName;
        this.fcmToken = fcmToken;
        this.aboutUser = aboutUser;
    }

    public postModel() {
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

}
