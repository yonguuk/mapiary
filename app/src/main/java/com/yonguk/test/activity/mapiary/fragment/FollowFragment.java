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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static final String URL_SERVER= "http://kktt0202.dothome.co.kr/test.php";


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
        Log.i("uks","follow : onCreate()");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_follow, container, false);
        mSwipeRefrechLayout = (SwipeRefreshLayout) mLinearLayout.findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.rv);
        mRVAdapter = new RVAdapter(getActivity(), cardDatas);
        mRecyclerView.setAdapter(mRVAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mSwipeRefrechLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        Log.i("uks", "Follow : onCreateView()");
        return mLinearLayout;

    }

    public void refreshContent(){
/*        mRVAdapter = new RVAdapter(getActivity(),getCardData());
        mRecyclerView.setAdapter(mRVAdapter);
        mSwipeRefrechLayout.setRefreshing(false);*/
    }

    public static String getRequestUrl(String userID){
        String requestUrl = URL_SERVER +"?id=" + userID;
        Log.i("uks",requestUrl);
        return requestUrl;
    }

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_SERVER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(getActivity(), response.toString(),Toast.LENGTH_LONG).show();
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks",error.getMessage());
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
                //Toast.makeText(getActivity(), data,Toast.LENGTH_LONG).show();
            }
        }catch(JSONException e){
            Log.i("uks",e.getMessage());
        }catch(Exception e){
            Log.i("uks",e.getMessage());
        }
    }

/*    private void parseJsonResponse(JSONObject response){
        if(response == null || response.length() == 0){
            return;
        }

        try{
            if(response.has("result")){
                JSONArray arrayResult = response.getJSONArray("result");
                for(int i=0; i<arrayResult.length(); i++){
                    JSONObject currentResult = arrayResult.getJSONObject(i);
                    String userID = currentResult.getString("user_id");
                    String imageMainUrl = currentResult.getString("img_url");
                    String content = currentResult.getString("content");
                    String date = currentResult.getString("date");
                    int like = currentResult.getInt("like");

                    RVCardData card = new RVCardData();
                    card.setUserID(userID);
                    card.setDate(date);
                    card.setTextContent(content);
                    card.setImageMainUrl(imageMainUrl);
                    card.setLike(like);
                    card.setImageProfileUrl(R.drawable.profile+"");

                    cardDatas.add(card);
                }
            }
        }catch(JSONException e){
            Log.i("uks",e.getMessage());
        }catch(Exception e){
            Log.i("uks",e.getMessage());
        }
    }*/
}
