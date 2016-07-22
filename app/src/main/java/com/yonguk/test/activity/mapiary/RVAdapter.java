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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-07-11.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {
    LayoutInflater inflater;
    ///List<RVData> data = Collections.emptyList();
    List<RVCardData> cardData = Collections.emptyList();
    Context context = null;

 /*   public RVAdapter(Context context,List<RVData> data){
        //여기 이해 안됨
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }*/

    public RVAdapter(Context context, List<RVCardData> cardData){
        inflater = LayoutInflater.from(context);
        this.cardData = cardData;
        this.context = context;
    }

    public void setCardList(ArrayList<RVCardData> cardData){
        this.cardData = cardData;
        notifyItemRangeChanged(0, cardData.size());
    }

    @Override
    public RVAdapter.RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item, parent,false);
        RVViewHolder holder = new RVViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.RVViewHolder holder, int position) {
        RVCardData curData = new RVCardData();
        curData = cardData.get(position);
        holder.img.setImageResource(Integer.parseInt(curData.imageUrl));
        holder.name.setText(curData.getName());
        holder.date.setText(curData.getDate());
        holder.textContent.setText(curData.getTextContent());

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


    class RVViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name;
        TextView date;
        TextView textContent;
        public RVViewHolder(View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            date = (TextView)itemView.findViewById(R.id.tv_date);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
/*            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "ImageView clicked", Toast.LENGTH_LONG).show();
                }
            });*/
        }

    }

    /*
    Steps to handle the recycler click
    1. Create a class that extends RecyclerView.OnItemTouchListener

    2. Create an interface inside that class that supports click and long click and indicates the View
    that was clicked and the position where it was clicked

    3. Create a GestureDetector to detect ACTION_UP single tap and long press events

    4. Return true from the singleTap to indicate your GestureDetector has consumed the event.

    5. Find the childView containing the coordinates specified by the MotionEvent and
       if the childView is not null and the listener is not null either, fire a long click event

    6. Use the onInterceptTouchEvnent of your RecccyclerView to check if the childView is not null,
       the listener is not null and the gesture detector consumed the touch event

    7. If above condition holds true, fire the click event
     */

}
