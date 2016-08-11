package com.yonguk.test.activity.mapiary.subactivity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardActivity extends AppCompatActivity implements View.OnClickListener{

    protected Toolbar toolbar = null;
    CircleImageView ivProfile;
    ImageView ivContent, ivLike, ivRe;
    TextView tvUserID,tvDate,tvTextContent,tvTextTitle,tvlike = null;

    private final String USER_ID = "user_id";
    private final String PROFILE_IMAGE_URL = "profile_url";
    private final String CONTENT_IMAGE_URL = "content_url";
    private final String LIKE = "like";
    private final String TITLE = "title";
    private final String TEXT_CONTENT = "text_content";
    private final String Date = "date";

    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;

    String userID, profileImageUrl, contentImageUrl, like, title, textContent, date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();

        Intent intent = getIntent();

        userID = intent.getStringExtra(USER_ID);
        profileImageUrl = intent.getStringExtra(PROFILE_IMAGE_URL);
        contentImageUrl = intent.getStringExtra(CONTENT_IMAGE_URL);
        like = intent.getStringExtra(LIKE);
        title = intent.getStringExtra(TITLE);
        textContent = intent.getStringExtra(TEXT_CONTENT);
        date = intent.getStringExtra(Date);

        setView();
    }

    private void setView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mapiary");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProfile = (CircleImageView) findViewById(R.id.cardactivity_profile_image);
        ivContent = (ImageView) findViewById(R.id.cardactivity_iv_content);
        ivLike = (ImageView) findViewById(R.id.cardactivity_iv_like);
        ivRe = (ImageView) findViewById(R.id.cardactivity_iv_re);
        tvUserID = (TextView) findViewById(R.id.cardactivity_tv_user_id);
        tvDate = (TextView) findViewById(R.id.cardactivity_tv_date);
        tvTextContent = (TextView) findViewById(R.id.cardactivitv_text_content);
        tvTextTitle = (TextView)findViewById(R.id.cardactivity_text_title);
        tvlike = (TextView)findViewById(R.id.cardactivity_tv_like);

        tvUserID.setText(userID);
        tvDate.setText(date);
        tvTextContent.setText(textContent);
        tvTextTitle.setText(title);
        tvlike.setText(like);

        if(profileImageUrl != null){
            imageLoader.get(profileImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    ivProfile.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ivProfile.setImageResource(R.drawable.profile);
                }
            });
        }

        if(contentImageUrl != null){
            imageLoader.get(contentImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    ivContent.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ivContent.setImageResource(R.drawable.image3);
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }
}
