package com.yonguk.test.activity.mapiary.subactivity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardActivity extends AppCompatActivity implements View.OnClickListener{

    VideoView videoView;
    private final String USER_ID = "user_id";
    private final String PROFILE_IMAGE_URL = "profile_url";
    //private final String CONTENT_IMAGE_URL = "content_url";
    private final String VIDEO_URL = "video_url";
    private final String LIKE = "like";
    private final String TITLE = "title";
    private final String TEXT_CONTENT = "text_content";
    private final String Date = "date";

    private VolleySingleton volleySingleton = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;

    String userID, profileImageUrl, videoUrl, like, title, textContent, date = null;

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
        //contentImageUrl = intent.getStringExtra(CONTENT_IMAGE_URL);
        videoUrl = intent.getStringExtra("video_url");
        Log.i("Video Url : ", videoUrl);
        //like = intent.getStringExtra(LIKE);
        title = intent.getStringExtra(TITLE);
        textContent = intent.getStringExtra(TEXT_CONTENT);
        date = intent.getStringExtra(Date);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(videoUrl);
        final MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
