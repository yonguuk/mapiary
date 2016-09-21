package com.yonguk.test.activity.mapiary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.data.RVTrackingData;

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
    JSONObject json;
    public RVTrackingAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private void setDataList(ArrayList<RVTrackingData> dataList){

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

        //holder.iv.
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TrackingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv;

        public TrackingViewHolder(View itemView){
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_tracking);
            iv.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
