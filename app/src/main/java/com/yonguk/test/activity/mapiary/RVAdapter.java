package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * Created by dosi on 2016-07-11.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {
    LayoutInflater inflater;
    List<RVData> data = Collections.emptyList();
    Context context = null;

    public RVAdapter(Context context,List<RVData> data){
        //여기 이해 안됨
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public RVAdapter.RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item, parent,false);
        RVViewHolder holder = new RVViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.RVViewHolder holder, int position) {
        RVData curData = new RVData();
        curData = data.get(position);
        holder.img.setImageResource(curData.viewId);
        holder.title.setText(curData.s);

        //여기서 리스너 달아도 됨
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void deleteItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }


    class RVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView img;
        TextView title;
        public RVViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);

            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            deleteItem(getLayoutPosition());
        }
    }
}
