package com.yonguk.test.activity.mapiary;

/**
 * Created by dosi on 2016-07-20.
 */
public class RVCardData {

    String imageMainUrl = null;
    String imageProfileUrl = null;
    String name = null;
    String date = null;
    String textContent = null;

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

    public void setTextContent(String textContent){
        this.textContent = textContent;
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

    public String getTextContent(){
        return textContent;
    }
}
