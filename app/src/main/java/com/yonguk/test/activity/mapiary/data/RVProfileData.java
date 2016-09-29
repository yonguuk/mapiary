package com.yonguk.test.activity.mapiary.data;

import android.graphics.Bitmap;

/**
 * Created by dosi on 2016-08-18.
 */
public class RVProfileData {
    private String userID;
    private String stateMessage;
    private String following;
    private String follower;
    private String profile_url;
    private Bitmap profile_bitmap;
    private String card;


    public Bitmap getProfile_bitmap() {
        return profile_bitmap;
    }

    public void setProfile_bitmap(Bitmap profile_bitmap) {
        this.profile_bitmap = profile_bitmap;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }
}
