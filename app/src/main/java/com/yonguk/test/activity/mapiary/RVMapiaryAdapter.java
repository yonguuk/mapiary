package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.maps.MapView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-08-17.
 */
public class RVMapiaryAdapter extends RecyclerView.Adapter<RVMapiaryAdapter.MapiaryViewHolder> {
    private LayoutInflater inflater;
    public RVMapiaryAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MapiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_mapiary_item,parent,false);
        return null;
    }

    @Override
    public void onBindViewHolder(MapiaryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MapiaryViewHolder extends RecyclerView.ViewHolder{

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
            textTitle = (TextView) itemView.findViewById(R.id.tv_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
