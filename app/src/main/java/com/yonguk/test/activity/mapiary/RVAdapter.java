package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-07-11.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {
    LayoutInflater inflater;
    List<RVCardData> cardData = Collections.emptyList();
    Context context = null;
    VolleySingleton volleySingleton;
    ImageLoader imageLoader;

    public RVAdapter(Context context, List<RVCardData> cardData){
        inflater = LayoutInflater.from(context);
        this.cardData = cardData;
        this.context = context;
        volleySingleton = VolleySingleton.getInstance(context);
        imageLoader = volleySingleton.getImageLoader();
    }

    public void setCardList(ArrayList<RVCardData> cardData){
        this.cardData = cardData;
        notifyItemRangeChanged(0, cardData.size());
    }

    @Override
    public RVAdapter.RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item, parent,false);
        RVViewHolder holder = new RVViewHolder(view);
        Log.i("uks", "onCreateViewHolder()");
        return holder;
    }

    @Override
    public void onBindViewHolder(final RVAdapter.RVViewHolder holder, int position) {
        try {
            RVCardData curData = new RVCardData();
            curData = cardData.get(position);
            //holder.ivProfile.setImageResource(Integer.parseInt(curData.imageProfileUrl));
            holder.userID.setText(curData.getUserID());
            holder.date.setText(curData.getDate());
            holder.textTitle.setText(curData.getTextTitle());
            holder.textContent.setText(curData.getTextContent());
            holder.like.setText(curData.getLike()+"");
 /*           String imageProfileUrl = curData.getImageProfileUrl();
            if(imageProfileUrl != null){
                imageLoader.get(imageProfileUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }*/

            String imageProfileUrl = curData.getImageProfileUrl();
            Log.i("uks","profile url : " + imageProfileUrl);
            if(imageProfileUrl != null){
                imageLoader.get(imageProfileUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.ivProfile.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("uks","profile error");
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
                        //default image

                        holder.ivContent.setImageResource(R.drawable.image3);
                    }
                });
            }


        }catch(Exception e){
            Log.i("uks",e.getMessage());
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


    class RVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView ivProfile;
        ImageView ivContent;
        TextView userID;
        TextView date;
        TextView textContent;
        TextView textTitle;
        TextView like;
        ImageView btn1,btn2,btn3;
        public RVViewHolder(View itemView) {
            super(itemView);
            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ivContent = (ImageView)itemView.findViewById(R.id.iv_content);
            userID = (TextView) itemView.findViewById(R.id.tv_user_id);
            date = (TextView)itemView.findViewById(R.id.tv_date);
            textTitle = (TextView) itemView.findViewById(R.id.tv_text_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
            like = (TextView) itemView.findViewById(R.id.tv_like);
            btn1 = (ImageView) itemView.findViewById(R.id.btn1);
            btn2 = (ImageView)itemView.findViewById(R.id.btn2);
            btn3 = (ImageView)itemView.findViewById(R.id.btn3);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"button clicked",Toast.LENGTH_LONG).show();
                }
            });
            ivContent.setOnClickListener(this);
            btn2.setOnClickListener(this);
/*            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "ImageView clicked", Toast.LENGTH_LONG).show();
                }
            });*/
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.profile_image:
                    Toast.makeText(context,getLayoutPosition() + "th ImageView Clicked",Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn2:
                    Toast.makeText(context, "button2 clicked", Toast.LENGTH_LONG).show();
            }
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
