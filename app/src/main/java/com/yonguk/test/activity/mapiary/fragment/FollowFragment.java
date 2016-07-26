package com.yonguk.test.activity.mapiary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.RVAdapter;
import com.yonguk.test.activity.mapiary.RVCardData;
import com.yonguk.test.activity.mapiary.RVData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dosi on 2016-07-18.
 */
public class FollowFragment extends Fragment{

    RecyclerView mRecyclerView = null;
    SwipeRefreshLayout mSwipeRefrechLayout = null;
    RVAdapter mRVAdapter = null;
    ImageView imageView = null;


    public static FollowFragment newInstance(){
        FollowFragment f = new FollowFragment();
        return f;
    }

    public static List<RVCardData> getCardData(){
        List<RVCardData> dataList = new ArrayList<RVCardData>();
        for(int i=0; i<5; i++){
            RVCardData temp = new RVCardData();
            temp.setName("조유섭");
            temp.setDate("2016. 7. 20");
            temp.setImageUrl(R.drawable.profile + "");
            temp.setTextContent("지금까지 사용했던 뷰 들과 다르게 두가지 뷰를 사용하려면 라이브러리를 추가하여야 합니다." +
                    "구글이 이클립스의 지원을 종료하기로 하였기 때문에 안드로이드 스튜디오를 기준으로 하겠습니다.");
            dataList.add(temp);

        }

        return dataList;
    }
/*

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

    public static List<RVData> getData2(){
        List<RVData> dataList = new ArrayList<RVData>();
        int[] imgId = {

                R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants,
                R.drawable.ic_favorites,R.drawable.ic_friends, R.drawable.ic_nearby, R.drawable.ic_recents, R.drawable.ic_restaurants};
        String[] titles = {

                "nearby","recents", "restaurants"
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
*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_follow, container, false);
        mSwipeRefrechLayout = (SwipeRefreshLayout) mLinearLayout.findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.rv);
        //mRVAdapter = new RVAdapter(getActivity(),getData());
        mRVAdapter = new RVAdapter(getActivity(),getCardData());
        mRecyclerView.setAdapter(mRVAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mSwipeRefrechLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        return mLinearLayout;
    }

    public void refreshContent(){
        mRVAdapter = new RVAdapter(getActivity(),getCardData());
        mRecyclerView.setAdapter(mRVAdapter);
        mSwipeRefrechLayout.setRefreshing(false);
    }

}
