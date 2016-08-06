package com.yonguk.test.activity.mapiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.RVAdapter;
import com.yonguk.test.activity.mapiary.RVCardData;
import com.yonguk.test.activity.mapiary.SampleChildActivity;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dosi on 2016-07-18.
 */
public class MainFragment extends Fragment {

    RecyclerView mRecyclerView = null;
    SwipeRefreshLayout mSwipeRefrechLayout = null;
    RVAdapter mRVAdapter = null;
    private ArrayList<RVCardData> cardDatas = new ArrayList<>();
    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;
    String userID=null;

    final String URL_SERVER= "http://kktt0202.dothome.co.kr/master/contents/random_card.php";

    public static MainFragment newInstance(){
        MainFragment f = new MainFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance(getActivity());
        requestQueue = volleySingleton.getRequestQueue();
        Bundle bundle = this.getArguments();
        userID = bundle.getString("USER_ID");

        Log.i("uks", "main : onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView)mLinearLayout.findViewById(R.id.main_rv);
        mSwipeRefrechLayout = (SwipeRefreshLayout) mLinearLayout.findViewById(R.id.main_swipe_layout);
        mRVAdapter = new RVAdapter(getActivity());
        mRecyclerView.setAdapter(mRVAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefrechLayout.setRefreshing(false);
        mSwipeRefrechLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        sendJsonRequest();
        return mLinearLayout;
    }

    public void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_SERVER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                cardDatas = parseJsonResponse(response);
                mRVAdapter.setCardList(cardDatas);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
/*                Map<String,String> params = new HashMap<String,String>();
                params.put(KEY_USER_ID, userID);*/
                return super.getParams();
            }
        };
        requestQueue.add(request);
    }

    private ArrayList<RVCardData> parseJsonResponse(JSONObject response){
        ArrayList<RVCardData> list = new ArrayList<>();
        if(response!=null || response.length() > 0) {

            try {
                if (response.has("result")) {
                    JSONArray arrayResult = response.getJSONArray("result");
                    for (int i = 0; i < arrayResult.length(); i++) {
                        JSONObject currentResult = arrayResult.getJSONObject(i);
                        String user_id = currentResult.getString("user_id");
                        String date = currentResult.getString("date");
                        String profileImageUrl = currentResult.getString("profile_url");
                        String contentImageUrl = currentResult.getString("img_url");
                        String textContent = currentResult.getString("content");
                        String textTitle = currentResult.getString("title");
                        int like = currentResult.getInt("like");

                        RVCardData card = new RVCardData();
                        card.setUserID(user_id);
                        card.setDate(date);
                        card.setTextContent(textContent);
                        card.setTextTitle(textTitle);
                        card.setImageMainUrl(contentImageUrl);
                        card.setLike(like);
                        card.setImageProfileUrl(profileImageUrl);

                        list.add(card);
                    }
                }
            } catch (JSONException e) {
                Log.i("uks", e.getMessage());
            } catch (Exception e) {
                Log.i("uks", e.getMessage());
            }
        }
        return list;
    }

    public void refreshContent(){
        cardDatas.clear();
        sendJsonRequest();
        mRVAdapter.setCardList(cardDatas);
        mSwipeRefrechLayout.setRefreshing(false);
    }

}
