package com.example.ichat.Post_Maintainence;

public class CommentModel {
    String userProfile,userName,userComment;

    public CommentModel() {
    }

    public CommentModel(String userProfile, String userName, String userComment) {
        this.userProfile = userProfile;
        this.userName = userName;
        this.userComment = userComment;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
}
