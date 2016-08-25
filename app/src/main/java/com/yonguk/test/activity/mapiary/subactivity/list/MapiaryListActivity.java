package com.yonguk.test.activity.mapiary.subactivity.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.RVMapiaryAdapter;
import com.yonguk.test.activity.mapiary.RVMapiaryData;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.subactivity.MapiaryUploadActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapiaryListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView = null;
    RVMapiaryAdapter mRVMapiaryAdapter = null;
    ArrayList<RVMapiaryData> mapiaryDatas = null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    private String userID = "";
    private final String USER_ID = "user_id";
    Context mContext = null;
    private final int REQUEST_CODE_UPLOAD_MAPIARY = 1;
    private final String TAG = "MapiaryListActivity";
    private final String URL_SERVER = "http://kktt0202.dothome.co.kr/master/contents/mapiary_card.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapiary_list);
        mContext = this;
        Intent intent = getIntent();
        userID  = intent.getStringExtra(USER_ID);
        Log.i(TAG, userID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mapiary");
        mapiaryDatas = new ArrayList<>();

        volleySingleton = VolleySingleton.getInstance(mContext);
        requestQueue = volleySingleton.getRequestQueue();
        mRecyclerView = (RecyclerView) findViewById(R.id.mapiary_rv);
        mRVMapiaryAdapter = new RVMapiaryAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mRVMapiaryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MapiaryUploadActivity.class);
                intent.putExtra(USER_ID,userID);
                startActivityForResult(intent, REQUEST_CODE_UPLOAD_MAPIARY);
            }
        });

        sendJsonRequest();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_UPLOAD_MAPIARY && resultCode == RESULT_OK){
/*          RVMapiaryData mapiaryData = new RVMapiaryData();
            mapiaryData.setUserID(data.getStringExtra("USER_ID"));
            mapiaryData.setTextTitle(data.getStringExtra("TITLE"));
            mapiaryData.setTextContent(data.getStringExtra("CONTENT"));
            mapiaryDatas.add(mapiaryData);
            mRVMapiaryAdapter.setList(mapiaryDatas);*/
        }
    }

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_SERVER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                mapiaryDatas = parseJsonResponse(response);
                mRVMapiaryAdapter.setList(mapiaryDatas);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        requestQueue.add(request);
    }

    private ArrayList<RVMapiaryData> parseJsonResponse(JSONObject response) {
        ArrayList<RVMapiaryData> list =  new ArrayList<>();
        if(response != null || response.length() > 0){
            try{
                if(response.has("result")){
                    JSONArray arrayResult = response.getJSONArray("result");
                    for(int i=0; i<arrayResult.length(); i++){
                        JSONObject currentResult = arrayResult.getJSONObject(i);
                        String user_id = currentResult.getString("user_id");
                        String date = currentResult.getString("date");
                        String profileImageUrl = currentResult.getString("profile_url");
                        String textContent = currentResult.getString("content");
                        String textTitle = currentResult.getString("title");
                        int like = currentResult.getInt("like");

                        RVMapiaryData mapiary = new RVMapiaryData();
                        mapiary.setUserID(user_id);
                        mapiary.setDate(date);
                        mapiary.setProfileUrl(profileImageUrl);
                        mapiary.setTextContent(textContent);
                        mapiary.setTextTitle(textTitle);
                        list.add(mapiary);
                    }
                }
            }catch (JSONException e) {
                Log.i(TAG, e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
        return list;
    }


}
