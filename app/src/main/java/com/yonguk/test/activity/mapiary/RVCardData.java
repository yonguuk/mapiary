package com.yonguk.test.activity.mapiary;

/**
 * Created by dosi on 2016-07-20.
 */
public class RVCardData {

    String imageUrl = null;
    String name = null;
    String date = null;
    String textContent = null;

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
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

    public String getImageUrl(){
        return imageUrl;
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
