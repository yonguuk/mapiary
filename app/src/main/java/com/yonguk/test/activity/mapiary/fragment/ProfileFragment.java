package com.yonguk.test.activity.mapiary.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.data.RVCardData;
import com.yonguk.test.activity.mapiary.adapter.RVProfileAdapter;
import com.yonguk.test.activity.mapiary.data.RVProfileData;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dosi on 2016-07-18.
 */
public class ProfileFragment extends Fragment{

    LinearLayout mapiary;
    RecyclerView mRecyclerView = null;
    RVProfileAdapter mRVProfileAdapter = null;
    SwipeRefreshLayout mSwipeRefrechLayout = null;
    LinearLayout root = null;
    String userID = null;
    Bitmap bitmap = null;
    RVProfileData profileDatas = null;
    ArrayList<RVCardData> cardDatas = null;
    private String cachedImageUrl = "";
    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;

    private final String TAG = "ProfileFragment";
    private final int PICK_IMAGE_REQUEST = 2;
    private final String REQUEST_HEADER_URL = "http://kktt0202.dothome.co.kr/master/user/profile.php";
    private final String REQUEST_URL = "http://kktt0202.dothome.co.kr/master/contents/my_contents.php";
    private final String UPLOAD_PROFILE_URL = "http://kktt0202.dothome.co.kr/master/upload/upload_profile.php";
    private final String KEY_USER_ID = "user_id";
    private final String KEY_STATE = "state_message";
    private final String KEY_CARDS = "cards";
    private final String KEY_FOLLOWING = "following";
    private final String KEY_FOLLOWER ="follower";
    private final String KEY_PROFILE_URL = "profile_url";
    private final String KEY_UPLOAD_PROFILE_IMAGE = "profile_image";

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
        cachedImageUrl = "http://kktt0202.dothome.co.kr/master/upload/image_profile" + "/" + userID + ".png";
        Log.i("uks", "Profile : onCreate()");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_profile,container,false);
        root = mLinearLayout;
        mSwipeRefrechLayout = (SwipeRefreshLayout) mLinearLayout.findViewById(R.id.profile_swipe_layout);
        mRecyclerView = (RecyclerView) mLinearLayout.findViewById(R.id.rv_profile);
        mRVProfileAdapter = new RVProfileAdapter(getActivity(),userID);
        Log.d(TAG,userID);

        mRecyclerView.setAdapter(mRVProfileAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
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


    private void sendJsonRequest(){
        JsonObjectRequest headerRequest = new JsonObjectRequest(Request.Method.GET, getHeaderRequestUrl(userID), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                profileDatas = parseHeaderJsonResponse(response);
                mRVProfileAdapter.setProfileData(profileDatas);
                Log.d("uks", "profile info : " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","Profile: " + error.getMessage());
            }
        });

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(userID), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cardDatas = parseJsonResponse(response);
                mRVProfileAdapter.setCardList(cardDatas);
                Log.d("uks","profile card : " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("uks","profile card error : " + error.toString());
            }
        });
        requestQueue.add(headerRequest);
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

    private RVProfileData parseHeaderJsonResponse(JSONObject response){
        RVProfileData profile = new RVProfileData();
        String user_id="";
        String state_message="";
        String cards="";
        String following="";
        String follower ="";
        String profile_url="";
        if(response != null || response.length() > 0){

            try{
                if(response.has("result")){
                    JSONArray arrayResult = response.getJSONArray("result");
                    for(int i=0; i<arrayResult.length(); i++){
                        JSONObject currentResult = arrayResult.getJSONObject(i);
                        user_id = currentResult.getString(KEY_USER_ID);
                        state_message = currentResult.getString(KEY_STATE);
                        //cards = currentResult.getString(KEY_CARDS);
                        following = currentResult.getString(KEY_FOLLOWING);
                        follower = currentResult.getString(KEY_FOLLOWER);
                        profile_url = currentResult.getString(KEY_PROFILE_URL);

                        profile.setUserID(user_id);
                        profile.setStateMessage(state_message);
                        profile.setFollower(follower);
                        profile.setFollowing(following);
                        profile.setProfile_url(profile_url);
                    }
                }
            }catch (Exception e){

            }
        }
        return profile;
    }


    /**handle profile image request**/
    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_IMAGE_REQUEST){
            if(resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
                Uri filePath = data.getData();
                try{
                    //Getting bitmap from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                    //Setting the Bitmaq to ImageView
                    //circleProfileImage.setImageBitmap(bitmap);
                    //profileDatas.setProfile_bitmap(bitmap);
                    uploadImage();
                }catch(IOException e){
                    Log.i("uks", e.getMessage());
                }catch(Exception e){
                    Log.i("uks", e.getMessage());
                }
            }
        }
    }


    private String getHeaderRequestUrl(String userID){
        return REQUEST_HEADER_URL + "?user_id=" + userID;
    }

    private String getRequestUrl(String userID){
        return REQUEST_URL + "?user_id=" + userID;
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage(){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...", "Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Snackbar.make(root,"프로필 사진이 변경되었습니다.", Snackbar.LENGTH_LONG).show();
                Log.i("uks", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Snackbar.make(root,"서버와 연결에 실패하였습니다",Snackbar.LENGTH_LONG).show();
                Log.i("uks", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                Map<String, String> params = new HashMap<String,String>();
                params.put(KEY_UPLOAD_PROFILE_IMAGE,image);
                params.put(KEY_USER_ID,userID);
                return params;
            }
        };

        requestQueue.add(stringRequest);


    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

    public void refreshContent(){
        cardDatas.clear();
        //requestQueue.getCache().remove(cachedImageUrl);

        sendJsonRequest();
        mRVProfileAdapter.setCardList(cardDatas);
        mSwipeRefrechLayout.setRefreshing(false);
    }

}
