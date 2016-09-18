package com.yonguk.test.activity.mapiary.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.data.RVCardData;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.subactivity.CardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-07-11.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {
    LayoutInflater inflater;
    List<RVCardData> cardData = Collections.emptyList();
    Context context = null;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    ImageLoader imageLoader;
    JSONObject resultJson = null;
    ArrayList<LatLng> points = null;

    private final String USER_ID = "user_id";
    private final String PROFILE_IMAGE_URL = "profile_url";
    private final String CONTENT_IMAGE_URL = "content_url";
    private final String LIKE = "like";
    private final String TITLE = "title";
    private final String TEXT_CONTENT = "text_content";
    private final String Date = "date";

    private final String TAG = "RVAdapter";


/*    *//*animation*//*
    private static final int ANIMATED_ITEM_COUNT = 2;
    private int lastAnimatedPosition =-1;
    private int itemsCount = 0;*/

    public RVAdapter(Context context, List<RVCardData> cardData){
        inflater = LayoutInflater.from(context);
        this.cardData = cardData;
        this.context = context;
        volleySingleton = VolleySingleton.getInstance(context);
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();
    }

    public RVAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance(context);
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();
    }

    public void setCardList(ArrayList<RVCardData> cardData){
        this.cardData = cardData;
        //notifyItemRangeChanged(0, cardData.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public RVAdapter.RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
/*        View itemView;
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        } else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.)
        }
        */
        View view = inflater.inflate(R.layout.recyclerview_item, parent,false);
        RVViewHolder holder = new RVViewHolder(view);
        Log.i(TAG, "onCreateViewHolder()");
        return holder;
    }

    @Override
    public void onBindViewHolder(final RVAdapter.RVViewHolder holder, int position) {

        RVCardData curData = new RVCardData();
        curData = cardData.get(position);
        getJSONFromUrl(curData.getLocationUrl());
        holder.userID.setText(curData.getUserID());
        holder.date.setText(curData.getDate());
        holder.textContent.setText(curData.getTextContent());
        try {
            points = parseJsonLocation(resultJson);
            holder.location.setText(getAddress(points.get(0).getLatitude(), points.get(0).getLongitude()));
        }catch (Exception e){
            Log.i(TAG, e.toString());
        }
        String imageProfileUrl = curData.getImageProfileUrl();
        Log.i(TAG,"profile url : " + imageProfileUrl);
        if(imageProfileUrl != null){
            imageLoader.get(imageProfileUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.ivProfile.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG,"profile error");
                    holder.ivProfile.setImageResource(R.drawable.profile);
                }
            });
        }

        String imageContentUrl = curData.getImageMainUrl();
        if (imageContentUrl != null) {
            imageLoader.get(imageContentUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.ivContent.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.ivContent.setImageResource(R.drawable.image3);
                }
            });
        }


        Log.i("uks","onBindViewHolder()");
        //여기서 리스너 달아도 됨
    }


    @Override
    public int getItemCount() {
        return cardData.size();
    }

    public void deleteItem(int position){
        cardData.remove(position);
        notifyItemRemoved(position);
    }


    public void getJSONFromUrl(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,"성공");
                Log.i(TAG,response.toString());
                resultJson = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"실패");
                Log.i(TAG,error.toString());
                resultJson = null;
            }
        });
        requestQueue.add(request);
    }

    private ArrayList<LatLng> parseJsonLocation(JSONObject jsonLocation){
        ArrayList<LatLng> points = new ArrayList<>();

        try{
            JSONObject json = jsonLocation;
            //JSONArray features = json.getJSONArray("features");
            //JSONObject feature = features.getJSONObject(0);
            JSONObject geometry = json.getJSONObject("geometry");

            if(geometry != null){
                String type = geometry.getString("type");
                if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")){
                    JSONArray coords = geometry.getJSONArray("coordinates");
                    for(int i=0; i<coords.length(); i++){
                        JSONArray coord = coords.getJSONArray(i);
                        LatLng latLng = new LatLng(coord.getDouble(0),coord.getDouble(1));
                        points.add(latLng);
                        Log.i(TAG, "아" + points.get(i).getLatitude() + " , " +  points.get(i).getLongitude());
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG,"Excepting Parsing GeoJson: " + e.toString());
        }
        return points;
    }


    /** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
    public String getAddress(double lat, double lng){
        String address = null;
        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        try{
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(list == null){
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }

        if(list.size() > 0){
            Address addr = list.get(0);
            address =
                    //+ addr.getPostalCode() + " "
                     addr.getAdminArea() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare();
        }

        return address;



    }



    class RVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView ivProfile;
        ImageView ivContent;
        TextView userID;
        TextView date;
        TextView textContent;
        TextView location;
        //TextView textTitle;
        //TextView like;
        //ImageView btnLike, btnRe;

        public RVViewHolder(View itemView) {
            super(itemView);
            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ivContent = (ImageView)itemView.findViewById(R.id.iv_content);
            userID = (TextView) itemView.findViewById(R.id.tv_user_id);
            date = (TextView)itemView.findViewById(R.id.tv_date);
           // textTitle = (TextView) itemView.findViewById(R.id.tv_text_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
            location = (TextView)itemView.findViewById(R.id.tv_location);
            //like = (TextView) itemView.findViewById(R.id.tv_like);
            //btnLike = (ImageView) itemView.findViewById(R.id.btn_like);
            //btnRe = (ImageView)itemView.findViewById(R.id.btn_re);

            ivProfile.setOnClickListener(this);
            ivContent.setOnClickListener(this);
            //btnLike.setOnClickListener(this);
            //btnRe.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.profile_image:{
                    break;
                }

                case R.id.iv_content:{
                    try {
                        Intent intent = new Intent(context, CardActivity.class);
                        RVCardData selectedCard = cardData.get(getAdapterPosition());
                        intent.putExtra(USER_ID, selectedCard.getUserID());
                        intent.putExtra(Date, selectedCard.getDate());
                        intent.putExtra(PROFILE_IMAGE_URL, selectedCard.getImageProfileUrl());
                        intent.putExtra(TITLE, selectedCard.getTextTitle());
                        intent.putExtra(CONTENT_IMAGE_URL, selectedCard.getImageMainUrl());
                        intent.putExtra(TEXT_CONTENT, selectedCard.getTextContent());
                        intent.putExtra(LIKE, selectedCard.getLike());
                        intent.putExtra("video_url", selectedCard.getVideoUrl());
                        //intent.putExtra("location_url", selectedCard.getLocationUrl());
                        context.startActivity(intent);
                    }catch(Exception e){
                        Log.d(TAG,e.getMessage());
                    }
                    break;
                }

            }
        }
    }

}
