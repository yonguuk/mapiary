package com.yonguk.test.activity.mapiary.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.tv.TvContentRating;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.SampleMapboxActivity;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.subactivity.MapiaryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-07-18.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView tvUserId, tvStatus, tvCards, tvFollowing, tvFollower, tvMapiary = null;
    LinearLayout mapiary;
    CircleImageView circleProfileImage = null;
    LinearLayout root = null;
    String userID = null;
    Bitmap bitmap = null;
    private String cachedImageUrl = "";
    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;

    private final int PICK_IMAGE_REQUEST = 1;
    private final String REQUEST_URL = "http://kktt0202.dothome.co.kr/master/user/profile.php";
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
        Log.i("uks", "Profile : onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_profile, container, false);
        root = mLinearLayout;
        tvUserId = (TextView) mLinearLayout.findViewById(R.id.profile_user_id);
        tvStatus = (TextView) mLinearLayout.findViewById(R.id.profile_status);
        tvCards = (TextView) mLinearLayout.findViewById(R.id.profile_card_num);
        tvFollowing = (TextView) mLinearLayout.findViewById(R.id.profile_following_num);
        tvFollower = (TextView) mLinearLayout.findViewById(R.id.profile_follower_num);
        mapiary = (LinearLayout) mLinearLayout.findViewById(R.id.profile_mapiary);
        //tvMapiary = (TextView) mLinearLayout.findViewById(R.id.profile_mapiary);
        circleProfileImage = (CircleImageView) mLinearLayout.findViewById(R.id.profile_profile_image);

        circleProfileImage.setOnClickListener(this);
        tvCards.setOnClickListener(this);
        tvFollowing.setOnClickListener(this);
        tvFollower.setOnClickListener(this);
        //tvMapiary.setOnClickListener(this);
        mapiary.setOnClickListener(this);

        sendJsonRequest();
        Log.i("uks", "Profile :  onCreateView()");
        return mLinearLayout;
    }

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(userID), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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
        String user_id="";
        String state_message="";
        String cards="";
        String following="";
        String follower ="";
        String profile_url="";
        if(response == null || response.length() == 0){
            return;
        }
        try{
            if(response.has("result")) {
                JSONArray arrayResult = response.getJSONArray("result");
                for(int i=0; i<arrayResult.length(); i++){
                    JSONObject currentResult = arrayResult.getJSONObject(i);
                    user_id = currentResult.getString(KEY_USER_ID);
                    state_message = currentResult.getString(KEY_STATE);
                    //cards = currentResult.getString(KEY_CARDS);
                    following = currentResult.getString(KEY_FOLLOWING);
                    follower = currentResult.getString(KEY_FOLLOWER);
                    profile_url = currentResult.getString(KEY_PROFILE_URL);
                    cachedImageUrl = profile_url;
                }

                tvUserId.setText(user_id);
                tvStatus.setText(state_message);
                //tvCards.setText(cards);
                tvFollowing.setText(following);
                tvFollower.setText(follower);


                if(profile_url != null){
                    final String finalProfile_url = profile_url;
                    imageLoader.get(profile_url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            circleProfileImage.setImageBitmap(response.getBitmap());
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST){
            if(resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
                Uri filePath = data.getData();
                try{
                    //Getting bitmap from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                    //Setting the Bitmaq to ImageView
                    circleProfileImage.setImageBitmap(bitmap);
                    uploadImage();
                }catch(IOException e){
                    Log.i("uks", e.getMessage());
                }catch(Exception e){
                    Log.i("uks", e.getMessage());
                }
            }
        }
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

        //requestQueue.getCache().remove(cachedImageUrl);
        requestQueue.add(stringRequest);


    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profile_profile_image:{
                showFileChooser();
                break;
            }
            case R.id.profile_card_num:{
                break;
            }
            case R.id.profile_following_num:{
                break;
            }
            case R.id.profile_follower_num:{
                break;
            }
            case R.id.profile_mapiary:{
                Log.i("uks","mapiary clicked");
                Intent intent = new Intent(getActivity(), SampleMapboxActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


}
