package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-08-17.
 */
public class RVMapiaryAdapter extends RecyclerView.Adapter<RVMapiaryAdapter.MapiaryViewHolder> {
    private LayoutInflater inflater;
    private String TAG = "RVMapiaryActivity";
    List<RVMapiaryData> mapiaryDatas = Collections.emptyList();
    Context context = null;

    public RVMapiaryAdapter(Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
        Log.i(TAG, "RVMapiaryAdapter()");
    }
    @Override
    public MapiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_mapiary_item,parent,false);
        MapiaryViewHolder holder = new MapiaryViewHolder(view);
        Log.i(TAG, "onCreateViewHolder()");
        return holder;
    }

    @Override
    public void onBindViewHolder(MapiaryViewHolder holder, int position) {
        RVMapiaryData curData = mapiaryDatas.get(position);
        Log.d(TAG,curData.getUserID());
        holder.userID.setText(curData.getUserID());
        holder.date.setText(curData.getDate());
        holder.textContent.setText(curData.getTextContent());
        Log.i(TAG, curData.getTextContent());
        holder.textTitle.setText(curData.getTextTitle());
        Log.i(TAG, "onBindViewHolder()");
    }


    public void setList(ArrayList<RVMapiaryData> datas){
        this.mapiaryDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mapiaryDatas.size();
    }

    class MapiaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView ivProfile;
        ImageView ivMapbox;
        TextView userID;
        TextView date;
        TextView textContent;
        TextView textTitle;
        public MapiaryViewHolder(View itemView) {
            super(itemView);
            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ivMapbox = (ImageView) itemView.findViewById(R.id.iv_mapbox);
            userID = (TextView) itemView.findViewById(R.id.tv_user_id);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            textTitle = (TextView) itemView.findViewById(R.id.tv_text_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_mapbox:

                    break;
            }
        }
    }
}
