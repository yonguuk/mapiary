package com.yonguk.test.activity.mapiary.data;

/**
 * Created by dosi on 2016-07-20.
 */
public class RVCardData {

    String imageMainUrl = null;
    String imageProfileUrl = null;


    String videoUrl = null;
    String name = null;
    String date = null;
    String textTitle = null;
    String textContent = null;
    String locationUrl = null;

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    String userID = null;
    int like=0;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public void setImageProfileUrl(String imageUrl){
        this.imageProfileUrl = imageUrl;
    }
    public void setImageMainUrl(String imageMainUrl){
        this.imageMainUrl = imageMainUrl;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setTextTitle(String textTitle){
        this.textTitle = textTitle;
    }
    public void setTextContent(String textContent){
        this.textContent = textContent;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setLike(int like){
        this.like = like;
    }
    public String getImageProfileUrl(){
        return imageProfileUrl;
    }

    public String getImageMainUrl(){
        return imageMainUrl;
    }

    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public String getTextTitle(){
        return textTitle;
    }
    public String getTextContent(){
        return textContent;
    }

    public String getUserID(){
        return userID;
    }

    public int getLike(){
        return like;
    }
}
