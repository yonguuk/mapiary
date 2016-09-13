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

public class CardActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback{

    protected Toolbar toolbar = null;
    CircleImageView ivProfile;
   // ImageView ivContent, ivLike, ivRe;
    TextView tvUserID,tvDate,tvTextContent,tvTextTitle,tvlike = null;
    //WebView webView;
    VideoView videoView;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
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
        MapboxAccountManager.start(this, ACCESS_TOKEN);
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
        like = intent.getStringExtra(LIKE);
        title = intent.getStringExtra(TITLE);
        textContent = intent.getStringExtra(TEXT_CONTENT);
        date = intent.getStringExtra(Date);
        final ScrollView scroll = (ScrollView) findViewById(R.id.cardactivitv_scroll);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        setView();

        mapView=(MapView)findViewById(R.id.cardactivity_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        scroll.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scroll.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return mapView.onTouchEvent(motionEvent);
            }
        });
    }

    private void setView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mapiary");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProfile = (CircleImageView) findViewById(R.id.cardactivity_profile_image);
        //ivContent = (ImageView) findViewById(R.id.cardactivity_iv_content);
        //webView = (WebView) findViewById(R.id.webview);
        videoView = (VideoView) findViewById(R.id.video_view);
        //ivLike = (ImageView) findViewById(R.id.cardactivity_iv_like);
        //ivRe = (ImageView) findViewById(R.id.cardactivity_iv_re);
        tvUserID = (TextView) findViewById(R.id.cardactivity_tv_user_id);
        tvDate = (TextView) findViewById(R.id.cardactivity_tv_date);
        tvTextContent = (TextView) findViewById(R.id.cardactivitv_text_content);
        tvTextTitle = (TextView)findViewById(R.id.cardactivity_text_title);
        //tvlike = (TextView)findViewById(R.id.cardactivity_tv_like);
        //scroll = (ScrollView) findViewById(R.id.cardactivitv_scroll);

        tvUserID.setText(userID);
        tvDate.setText(date);
        tvTextContent.setText(textContent);
        tvTextTitle.setText(title);
        //tvlike.setText(like);
        //webView.setWebViewClient(new WebViewClient());
        //webView.loadUrl(videoUrl);

        videoView.setVideoPath(videoUrl);
        final MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        //videoView.seekTo(500);
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

   /*     if(contentImageUrl != null){
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
*/

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
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(37.5806, 126.8882))
                        .zoom(15)
                        .tilt(20)
                        .build()

        ));
        mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.5806, 126.8882))
                        .title("I am here")
                        .snippet("welcome to my marker")


        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
