package com.example.ichat.UsersActivities;

public class ActivityModel {
    String profileUri,activity,date;

    public String getProfileUri() {
        return profileUri;
    }

    public ActivityModel() {
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActivityModel(String profileUri, String activity, String date) {
        this.profileUri = profileUri;
        this.activity = activity;
        this.date = date;
    }
}
