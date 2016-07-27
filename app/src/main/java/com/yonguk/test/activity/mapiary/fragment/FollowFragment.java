package com.yonguk.test.activity.mapiary.fragment;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.RVAdapter;
import com.yonguk.test.activity.mapiary.RVCardData;
import com.yonguk.test.activity.mapiary.network.UrlEndpoint;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dosi on 2016-07-18.
 */
public class FollowFragment extends Fragment{

    RecyclerView mRecyclerView = null;
    SwipeRefreshLayout mSwipeRefrechLayout = null;
    RVAdapter mRVAdapter = null;
    private ArrayList<RVCardData> cardDatas = new ArrayList<>();
    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;


    public static FollowFragment newInstance(){
        FollowFragment f = new FollowFragment();
        return f;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleySingleton = VolleySingleton.getInstance(getActivity());
        requestQueue = volleySingleton.getRequestQueue();

        sendJsonRequest();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_follow, container, false);
        mSwipeRefrechLayout = (SwipeRefreshLayout) mLinearLayout.findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.rv);
        mRVAdapter = new RVAdapter(getActivity(),cardDatas);
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

    public static String getRequestUrl(){
        return UrlEndpoint.URL_SERVER;
    }

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(getActivity(), response.toString(),Toast.LENGTH_LONG).show();
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

    private void parseJsonResponse(JSONObject response){
        if(response==null || response.length()==0){
            return;
        }
        try {
            if(response.has("result")){
                StringBuilder data = new StringBuilder();
                JSONArray arrayResult = response.getJSONArray("result");
                //List
                for(int i=0; i<arrayResult.length(); i++){
                    JSONObject currentResult = arrayResult.getJSONObject(i);
                    String id = currentResult.getString("id");
                    String name= currentResult.getString("name");
                    String password = currentResult.getString("password");
                    RVCardData card = new RVCardData();
                    card.setName(name);
                    card.setDate(id);
                    card.setTextContent(password);
                    card.setImageMainUrl("http://kktt0202.dothome.co.kr/image/sample.jpg");
                    card.setImageProfileUrl(R.drawable.profile+"");
                    cardDatas.add(card);
                    data.append("id = " + id + "\n" + "name = " + name + "\n" + "password = " + password + "\n");
                }
                Toast.makeText(getActivity(), data,Toast.LENGTH_LONG).show();
            }
        }catch(JSONException e){
            Log.i("uks",e.getMessage());
        }
    }


    public static List<RVCardData> getCardData(){
        List<RVCardData> dataList = new ArrayList<RVCardData>();
        for(int i=0; i<5; i++){
            RVCardData temp = new RVCardData();
            temp.setName("조유섭");
            temp.setDate("2016. 7. 20");
            temp.setImageProfileUrl(R.drawable.profile + "");
            temp.setTextContent("지금까지 사용했던 뷰 들과 다르게 두가지 뷰를 사용하려면 라이브러리를 추가하여야 합니다." +
                    "구글이 이클립스의 지원을 종료하기로 하였기 때문에 안드로이드 스튜디오를 기준으로 하겠습니다.");
            dataList.add(temp);

        }

        return dataList;
    }


}
