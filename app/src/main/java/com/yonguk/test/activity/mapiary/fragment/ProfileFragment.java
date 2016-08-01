package com.yonguk.test.activity.mapiary.fragment;

import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-07-18.
 */
public class ProfileFragment extends Fragment {

    TextView tvUserId;
    TextView tvPassword;
    CircleImageView circleProfileImage;

    String userID = null;
    String user_id = null;
    String password = null;
    String profileImageUrl = null;


    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;
    private final String REQUEST_URL = "http://kktt0202.dothome.co.kr/master/user/profile.php";
    private final String KEY_ID = "user_id";
    public static ProfileFragment newInstance(){
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance(getActivity());
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();
        Bundle bundle = this.getArguments();
        userID = bundle.getString("USER_ID");
        Log.i("uks", "Profile : onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_profile,container,false);
        tvUserId = (TextView) mLinearLayout.findViewById(R.id.profile_user_id);
        tvPassword = (TextView) mLinearLayout.findViewById(R.id.profile_password);
        circleProfileImage = (CircleImageView) mLinearLayout.findViewById(R.id.profile_profile_image);
        sendJsonRequest();
        Log.i("uks", "Profile :  onCreateView()");
        return mLinearLayout;
    }

  /*  private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("uks","Profile : " + response.toString());
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","Profile : " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_ID,userID);
                return params;
            }
        };
        requestQueue.add(request);
    }*/

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(userID), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("uks","Profile : " + response.toString());
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","Profile : " + error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void parseJsonResponse(JSONObject response){
        if(response == null || response.length() == 0){
            return;
        }
        try{
            if(response.has("result")) {
                JSONArray arrayResult = response.getJSONArray("result");
                for(int i=0; i<arrayResult.length(); i++){
                    JSONObject currentResult = arrayResult.getJSONObject(i);
                    user_id = currentResult.getString("user_id");
                    password = currentResult.getString("password");
                    profileImageUrl = currentResult.getString("profile_url");
                    Log.i("uks","프로필 유알엘: " + profileImageUrl);
                }
                tvUserId.setText(user_id);
                tvPassword.setText(password);
                if(profileImageUrl != null){
                    imageLoader.get(profileImageUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            circleProfileImage.setImageBitmap(response.getBitmap());
                            //circleProfileImage.setImageResource(R.drawable.profile);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            circleProfileImage.setImageResource(R.drawable.profile);
                        }
                    });
                }
            }
        }catch(JSONException e){
            Log.i("uks","Profile : " + e.getMessage());
        }catch(Exception e){
            Log.i("uks","Profile : " + e.getMessage());
        }

    }
    private String getRequestUrl(String userID){
        return REQUEST_URL + "?user_id=" + userID;
    }
}
