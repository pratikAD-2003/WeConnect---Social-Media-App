package com.example.ichat;

public class ChatImgModel_ {
    String uri,senderIdImg;

    public ChatImgModel_() {
    }

    public String getUri() {
        return uri;
    }

    public String getSenderIdImg() {
        return senderIdImg;
    }

    public ChatImgModel_(String uri, String senderIdImg) {
        this.uri = uri;
        this.senderIdImg = senderIdImg;
    }

    public void setSenderIdImg(String senderIdImg) {
        this.senderIdImg = senderIdImg;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
