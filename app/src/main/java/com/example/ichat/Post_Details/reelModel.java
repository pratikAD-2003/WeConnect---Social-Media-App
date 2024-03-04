package com.example.ichat.Post_Details;

public class reelModel {
    String reel_time;
    String reel_uri;
    String reel_title;
    String user_name;
    String user_id;
    String user_pic;
    String postKey;
    String aboutUser;
    String fcmToken;

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    String displayName;

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public reelModel(String reel_time, String reel_uri, String reel_title, String user_name, String user_id, String user_pic, String postKey, String aboutUser, String fcmToken, String displayName) {
        this.reel_time = reel_time;
        this.reel_uri = reel_uri;
        this.reel_title = reel_title;
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_pic = user_pic;
        this.postKey = postKey;
        this.aboutUser = aboutUser;
        this.fcmToken = fcmToken;
        this.displayName = displayName;
    }

    public String getReel_time() {
        return reel_time;
    }

    public reelModel() {
    }

    public void setReel_time(String reel_time) {
        this.reel_time = reel_time;
    }

    public String getReel_uri() {
        return reel_uri;
    }

    public void setReel_uri(String reel_uri) {
        this.reel_uri = reel_uri;
    }

    public String getReel_title() {
        return reel_title;
    }

    public void setReel_title(String reel_title) {
        this.reel_title = reel_title;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }
}
