package com.yonguk.test.activity.mapiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.data.RVCardData;
import com.yonguk.test.activity.mapiary.data.RVProfileData;
import com.yonguk.test.activity.mapiary.fragment.ProfileFragment;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.subactivity.CardActivity;
import com.yonguk.test.activity.mapiary.subactivity.list.CardListActivity;
import com.yonguk.test.activity.mapiary.subactivity.list.FollowerListActivity;
import com.yonguk.test.activity.mapiary.subactivity.list.FollowingListActivity;
import com.yonguk.test.activity.mapiary.subactivity.list.MapiaryListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dosi on 2016-08-18.
 */
public class RVProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LayoutInflater inflater;
    List<RVCardData> cardData = Collections.emptyList();
    RVProfileData profileData = null;
    Context context = null;
    VolleySingleton volleySingleton;
    ImageLoader imageLoader;
    ProfileFragment profileFragment = null;
    String userID = "";

    private final String TAG = "RVProfileAdapter";
    private final int PICK_IMAGE_REQUEST = 2;

    private final String USER_ID = "user_id";
    private final String PROFILE_IMAGE_URL = "profile_url";
    private final String CONTENT_IMAGE_URL = "content_url";
    private final String LIKE = "like";
    private final String TITLE = "title";
    private final String TEXT_CONTENT = "text_content";
    private final String Date = "date";

    private static final int HEADER = 0;
    private static final int OTHER = 1;


    public RVProfileAdapter(Context context, String userID){
        this.context = context;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance(context);
        imageLoader = volleySingleton.getImageLoader();
        profileFragment = ProfileFragment.newInstance();
        this.userID = userID;
    }

    public void setCardList(ArrayList<RVCardData> cardData){
        this.cardData = cardData;
        //notifyItemRangeChanged(0, cardData.size());
        notifyDataSetChanged();
    }

    public void setProfileData(RVProfileData profileData){
        this.profileData = profileData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == HEADER){
            return HEADER;
        } else{
            return OTHER;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER){
            View view = inflater.inflate(R.layout.rv_header_profile, parent, false);
            ViewHolderHeader holder = new ViewHolderHeader(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.recyclerview_item,parent,false);
            ViewHolderRV holder = new ViewHolderRV(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderHeader){
            try {
                final ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
                holderHeader.tvUserId.setText(profileData.getUserID());
                holderHeader.tvStatus.setText(profileData.getStateMessage());
                holderHeader.tvFollower.setText(profileData.getFollower());
                holderHeader.tvFollowing.setText(profileData.getFollowing());
                String profileUrl = profileData.getProfile_url();

                if (profileUrl != null) {
                    final String finalProfile_url = profileUrl;
                    imageLoader.get(profileUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            holderHeader.circleProfileImage.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            holderHeader.circleProfileImage.setImageResource(R.drawable.profile);
                        }
                    });
                }

            }catch(Exception e){
                Log.i(TAG,"error");
            }
        }else if(holder instanceof ViewHolderRV){
            final ViewHolderRV holderRV = (ViewHolderRV)holder;
            RVCardData curData = new RVCardData();
            curData = cardData.get(position-1);
            holderRV.userID.setText(curData.getUserID());
            holderRV.date.setText(curData.getDate());
            //holderRV.textTitle.setText(curData.getTextTitle());
            holderRV.textContent.setText(curData.getTextContent());
            //holderRV.like.setText(curData.getLike()+"");

            String imageProfileUrl = curData.getImageProfileUrl();
            Log.i("uks","profile url : " + imageProfileUrl);
            if(imageProfileUrl != null){
                imageLoader.get(imageProfileUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holderRV.ivProfile.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("uks","profile error");
                        holderRV.ivProfile.setImageResource(R.drawable.profile);
                    }
                });
            }

            String imageContentUrl = curData.getImageMainUrl();
            if (imageContentUrl != null) {
                imageLoader.get(imageContentUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holderRV.ivContent.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holderRV.ivContent.setImageResource(R.drawable.image3);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return cardData.size()+1;
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvUserId, tvStatus, tvCards, tvFollowing, tvFollower, tvMapiary = null;
        LinearLayout mapiary,follower,following,card = null;
        CircleImageView circleProfileImage = null;

        public ViewHolderHeader(View itemView){
            super(itemView);
            tvUserId = (TextView) itemView.findViewById(R.id.profile_user_id);
            tvStatus = (TextView) itemView.findViewById(R.id.profile_status);
            tvCards = (TextView) itemView.findViewById(R.id.profile_card_num);
            tvFollowing = (TextView) itemView.findViewById(R.id.profile_following_num);
            tvFollower = (TextView) itemView.findViewById(R.id.profile_follower_num);
            tvMapiary = (TextView) itemView.findViewById(R.id.profile_mapiary_num);
            mapiary = (LinearLayout) itemView.findViewById(R.id.profile_mapiary);
            following = (LinearLayout) itemView.findViewById(R.id.profile_following);
            follower = (LinearLayout) itemView.findViewById(R.id.profile_follower);
            card = (LinearLayout) itemView.findViewById(R.id.profile_card);
            circleProfileImage = (CircleImageView) itemView.findViewById(R.id.profile_profile_image);

            circleProfileImage.setOnClickListener(this);
            mapiary.setOnClickListener(this);
            follower.setOnClickListener(this);
            following.setOnClickListener(this);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.profile_mapiary:{
                    Log.i("uks", "mapiary clicked");
                    Intent intent = new Intent(context, MapiaryListActivity.class);
                    intent.putExtra(USER_ID,userID);
                    context.startActivity(intent);
                    break;
                }

                case R.id.profile_following:{
                    Intent intent = new Intent(context, FollowingListActivity.class);
                    context.startActivity(intent);
                    break;
                }

                case R.id.profile_follower:{
                    Intent intent = new Intent(context, FollowerListActivity.class);
                    context.startActivity(intent);
                    break;
                }

                case R.id.profile_card:{
                    Intent intent = new Intent(context, CardListActivity.class);
                    context.startActivity(intent);
                    break;
                }

                case R.id.profile_profile_image:{
                    showFileChooser();
                }

            }
        }
        private void showFileChooser(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    class ViewHolderRV extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView ivProfile;
        ImageView ivContent;
        TextView userID;
        TextView date;
        TextView textContent;
        //TextView textTitle;
        //TextView like;
        //ImageView btnLike, btnRe;

        public ViewHolderRV(View itemView) {
            super(itemView);
            ivProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ivContent = (ImageView)itemView.findViewById(R.id.iv_content);
            userID = (TextView) itemView.findViewById(R.id.tv_user_id);
            date = (TextView)itemView.findViewById(R.id.tv_date);
            //textTitle = (TextView) itemView.findViewById(R.id.tv_text_title);
            textContent = (TextView) itemView.findViewById(R.id.tv_text_content);
            //like = (TextView) itemView.findViewById(R.id.tv_like);
           // btnLike = (ImageView) itemView.findViewById(R.id.btn_like);
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
                        RVCardData selectedCard = cardData.get(getLayoutPosition()-1);
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
    }
}
