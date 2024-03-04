package com.example.ichat;

public class chatModel {
    public static final int CHAT_LAYOUT = 0;
    public static final int IMAGE_LAYOUT = 1;
    public static final int VIDEO_LAYOUT = 2;
    public static final int AUDIO_LAYOUT = 3;
    public static final int REEL_LAYOUT = 4;
    public static final int POST_SHARE_LAYOUT = 5;
    public static final int VIDEO_POST_SHARE_LAYOUT = 6;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    int type;
    String message;
    String senderId;
    String time;
    String videoUri;
    String keyId;
    String audioUri;

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public chatModel(String senderId, String time, int type, String audioUri) {
        this.type = type;
        this.senderId = senderId;
        this.time = time;
        this.audioUri = audioUri;
    }

    public chatModel(String senderId, int type, String time, String videoUri) {
        this.type = type;
        this.senderId = senderId;
        this.time = time;
        this.videoUri = videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public chatModel(int type, String time, String imageUri, String senderId) {
        this.type = type;
        this.time = time;
        this.imageUri = imageUri;
        this.senderId = senderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    String imageUri;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public chatModel() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public chatModel(int type, String message, String senderId, String time, String keyId) {
        this.type = type;
        this.message = message;
        this.senderId = senderId;
        this.time = time;
        this.keyId = keyId;
    }

    String reel_time;
    String reel_uri;
    String reel_title;
    String user_name;
    String user_id;
    String user_pic;
    String postKey;
    String aboutUser;
    String fcmToken;
    String display;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public chatModel(int type, String reel_time, String reel_uri, String reel_title, String user_name, String senderId, String user_pic, String postKey, String aboutUser, String fcmToken, String time, String user_id, String display) {
        this.type = type;
        this.reel_time = reel_time;
        this.reel_uri = reel_uri;
        this.reel_title = reel_title;
        this.user_name = user_name;
        this.senderId = senderId;
        this.user_pic = user_pic;
        this.postKey = postKey;
        this.aboutUser = aboutUser;
        this.fcmToken = fcmToken;
        this.time = time;
        this.user_id = user_id;
        this.display = display;
    }

    public String getReel_time() {
        return reel_time;
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

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

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
}
