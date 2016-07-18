package com.yonguk.test.activity.mapiary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yonguk.test.activity.mapiary.R;

/**
 * Created by dosi on 2016-07-18.
 */
public class NewsFragment extends Fragment {

    public static NewsFragment newInstance(){
        NewsFragment f = new NewsFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news,container,false);
    }
}
