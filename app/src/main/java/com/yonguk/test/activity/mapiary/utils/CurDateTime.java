package com.yonguk.test.activity.mapiary.utils;


import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dosi on 2016-08-27.
 */
public class CurDateTime {
    public static String getCurDateTime(){
        String curDateTime = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        curDateTime = dateFormat.format(cal.getTime());
        Log.i("CurDateTime", curDateTime);
        return curDateTime;
    }
}
