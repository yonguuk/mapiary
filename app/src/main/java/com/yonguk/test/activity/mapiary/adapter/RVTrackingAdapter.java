package com.yonguk.test.activity.mapiary.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.data.RVTrackingData;
import com.yonguk.test.activity.mapiary.subactivity.TrackingActivity;
import com.yonguk.test.activity.mapiary.subactivity.UploadActivity;
import com.yonguk.test.activity.mapiary.utils.DBManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yonguk on 2016-09-19.
 */
public class RVTrackingAdapter extends RecyclerView.Adapter<RVTrackingAdapter.TrackingViewHolder> {
    LayoutInflater inflater;
    List<RVTrackingData> trackingDatas = Collections.emptyList();
    Context context = null;
    private String userID;
    JSONObject json;
    Bitmap thumbnail;
    DBManager dbManager;
    private final String KEY_ID = "user_id";
    private final String KEY_PATH = "path";
    private final String KEY_LOCATION = "location";
    private final String KEY_CARD_ID = "card_id";
    private final String KEY_TEXT ="content";
    private final String KEY_EMOTION = "emotion";
    private final String KEY_ADDRESS = "address";
    private static final String TAG = "RVTrackingAdapter";

    private final int EMOTION_RELAX = 1;
    private final int EMOTION_ACTIVE = 2;
    private final int EMOTION_STRESS = 3;

    public RVTrackingAdapter(Context context, String userID){
        this.context = context;
        inflater = LayoutInflater.from(context);
        trackingDatas = new ArrayList<>();
        this.userID = userID;
        dbManager = DBManager.getInstance(context);
    }

    public void setVideoList(List<RVTrackingData> trackingDatas){
        this.trackingDatas = trackingDatas;
        notifyDataSetChanged();
    }
    @Override
    public TrackingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_tracking_item, parent, false);
        TrackingViewHolder holder = new TrackingViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TrackingViewHolder holder, int position) {
        RVTrackingData curData = new RVTrackingData();
        curData = trackingDatas.get(position);
        thumbnail = ThumbnailUtils.createVideoThumbnail(curData.getPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        holder.iv.setImageBitmap(thumbnail);

        //holder.iv.
    }

    @Override
    public int getItemCount() {
        return trackingDatas.size();
    }

    public void deleteItem(int position){
        try {
            RVTrackingData selectedContent = trackingDatas.get(position);
            dbManager.delete("file_path=" + "'"+ selectedContent.getPath() + "'", null);
            trackingDatas.remove(position);
            notifyItemRemoved(position);
        }catch (Exception e){
            Log.i(TAG,"틀렸다 임마");
        }
    }

    private String getEmotionColor(int emotion){
        String color;
        if(emotion == EMOTION_RELAX){
            color = "#4CAF50";
        }else if(emotion == EMOTION_ACTIVE){
            color = "#9C27B0";
        }else if(emotion == EMOTION_STRESS){
            color = "#F44336";
        }else{
            color = "#9E9E9E";
        }
        return color;
    }


    class TrackingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv;
        ImageView ivPlay;
        ImageView ivDelete;
        ImageView ivEmotion;

        public TrackingViewHolder(View itemView){
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_tracking);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv.setOnClickListener(this);
            ivPlay.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            iv.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_play:{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    RVTrackingData selectedContent = trackingDatas.get(getAdapterPosition());
                    Uri data = Uri.parse(selectedContent.getPath());
                    intent.setDataAndType(data, "video/mp4");
                    context.startActivity(intent);
                    break;
                }

                case R.id.iv_delete:{
                    deleteItem(getAdapterPosition());
                    break;
                }

                case R.id.iv_tracking:{
                    Intent intent = new Intent(context, UploadActivity.class);
                    RVTrackingData selectedContent = trackingDatas.get(getAdapterPosition());
                    intent.putExtra(KEY_ID, userID);
                    intent.putExtra(KEY_PATH, selectedContent.getPath());
                    intent.putExtra(KEY_LOCATION, selectedContent.getLocation());
                    intent.putExtra(KEY_EMOTION, selectedContent.getEmotion());
                    context.startActivity(intent);
                    break;
                }
            }
        }

    }
}
