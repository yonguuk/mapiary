package com.yonguk.test.activity.mapiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dosi on 2016-07-08.
 */
public class SecondFragment extends Fragment {
    RecyclerView mRecyclerView = null;
    RVAdapter mRVAdapter = null;
    ImageView imageView = null;
    public static SecondFragment newInstance(){
        SecondFragment f = new SecondFragment();
        return f;
    }

    public static List<RVData> getData(){
        List<RVData> dataList = new ArrayList<RVData>();
        int[] imgId = {R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants};
        String[] titles = {"favorites", "friends", "nearby","recents", "restaurants"
        ,"favorites", "friends", "nearby","recents", "restaurants"
        ,"favorites", "friends", "nearby","recents", "restaurants"
        ,"favorites", "friends", "nearby","recents", "restaurants"
        ,"favorites", "friends", "nearby","recents", "restaurants"};

        for(int i=0; i<imgId.length; i++){
            RVData temp = new RVData();
            temp.viewId = imgId[i];
            temp.s = titles[i];
            dataList.add(temp);
        }
        return dataList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_second,container,false);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.rv);
        mRVAdapter = new RVAdapter(getActivity(),getData());
        mRecyclerView.setAdapter(mRVAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mLinearLayout;
    }
}
