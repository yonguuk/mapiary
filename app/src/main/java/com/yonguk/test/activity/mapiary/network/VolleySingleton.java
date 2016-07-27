package com.yonguk.test.activity.mapiary.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by dosi on 2016-07-18.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private VolleySingleton(Context mContext){
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache mCache = new LruCache(10);
            @Override
            public Bitmap getBitmap(String url) {
                return (Bitmap)mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url,bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(Context mContext){
        if(sInstance == null){
            sInstance = new VolleySingleton(mContext);
        }
        return sInstance;
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }


}
