package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.yonguk.test.activity.mapiary.animation.Utils;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.subactivity.CardActivity;

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

    private final String USER_ID = "user_id";
    private final String PROFILE_IMAGE_URL = "profile_url";
    private final String CONTENT_IMAGE_URL = "content_url";
    private final String LIKE = "like";
    private final String TITLE = "title";
    private final String TEXT_CONTENT = "text_content";
    private final String Date = "date";


/*    *//*animation*//*
    private static final int ANIMATED_ITEM_COUNT = 2;
    private int lastAnimatedPosition =-1;
    private int itemsCount = 0;*/

    public RVAdapter(Context context, List<RVCardData> cardData){
        inflater = LayoutInflater.from(context);
        this.cardData = cardData;
        this.context = context;
        volleySingleton = VolleySingleton.getInstance(context);
        imageLoader = volleySingleton.getImageLoader();
    }

    public RVAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance(context);
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
        Log.i("uks", "onCreateViewHolder()");
        return holder;
    }

    @Override
    public void onBindViewHolder(final RVAdapter.RVViewHolder holder, int position) {
/*        try {
            runEnterAnimation(holder.itemView, position);
        }catch (Exception e){
            Log.i("uks",e.getMessage());
        }*/
        RVCardData curData = new RVCardData();
        curData = cardData.get(position);
        holder.userID.setText(curData.getUserID());
        holder.date.setText(curData.getDate());
        holder.textTitle.setText(curData.getTextTitle());
        holder.textContent.setText(curData.getTextContent());
        //holder.like.setText(curData.getLike()+"");

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
                    holder.ivContent.setImageResource(R.drawable.image3);
                }
            });
        }


        Log.i("uks","onBindViewHolder()");
        //여기서 리스너 달아도 됨
    }

/*
    public void runEnterAnimation(View view, int position){
        if(position>= ANIMATED_ITEM_COUNT -1){
            return;
        }

        if(position > lastAnimatedPosition){
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }
*/

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
        //TextView like;
        //ImageView btnLike, btnRe;

        public RVViewHolder(View itemView) {
            super(itemView);
            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ivContent = (ImageView)itemView.findViewById(R.id.iv_content);
            userID = (TextView) itemView.findViewById(R.id.tv_user_id);
            date = (TextView)itemView.findViewById(R.id.tv_date);
            textTitle = (TextView) itemView.findViewById(R.id.tv_text_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
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
                        context.startActivity(intent);
                    }catch(Exception e){
                        Log.d("uks",e.getMessage());
                    }
                    break;
                }

            }
        }

/*        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn1:
                    Toast.makeText(context,getLayoutPosition() + "th ImageView Clicked",Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn2:
                    Toast.makeText(context, "button2 clicked", Toast.LENGTH_LONG).show();
                    break;
            }
        }*/
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
