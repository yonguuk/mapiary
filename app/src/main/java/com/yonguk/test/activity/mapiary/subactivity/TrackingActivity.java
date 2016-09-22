package com.yonguk.test.activity.mapiary.subactivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.adapter.RVTrackingAdapter;
import com.yonguk.test.activity.mapiary.data.RVTrackingData;
import com.yonguk.test.activity.mapiary.utils.DBManager;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private MapView mapView;
    private MapboxMap map;
    private RecyclerView recyclerView;
    private RVTrackingAdapter rvTrackingAdapter;
    private LocationServices locationServices;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    private String userID ="";
    private List<RVTrackingData> trackingDatas;
    private static final int PERMISSIONS_LOCATION = 0;
    private static final int REQUEST_VIDEO = 10;
    private static final String TAG = "TrackingActivity";
    private final String KEY_ID = "user_id";

    /** SQLITE**/
    DBManager dbManager;
    Button btnCapture;
    Button btnClear;
    static final String FILE_PATH = "file_path";
    static final String LOCATION = "location";
    static final String EMOTION = "emotion";
    static final String DATETIME = "datetime";
    private final String[] columns = new String[]{FILE_PATH, LOCATION, EMOTION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_tracking);
        context = this;
        trackingDatas = new ArrayList<>();
        Intent intent = getIntent();
        userID = intent.getStringExtra(KEY_ID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = DBManager.getInstance(context);

        btnCapture = (Button) findViewById(R.id.btn_capture);
        btnClear = (Button) findViewById(R.id.btn_clear);
        Button btn = (Button) findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"씨 발", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Clicked");
                Intent intent = new Intent(TrackingActivity.this, CameraActivity.class);
                startActivityForResult(intent,REQUEST_VIDEO);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_tracking);
        rvTrackingAdapter = new RVTrackingAdapter(this, userID);
        Cursor c = dbManager.query(columns, null, null, null, null, null);
        if(c !=null){
            while(c.moveToNext()){
                String filePath = c.getString(0);
                String location = c.getString(1);
                String emotion = c.getString(2);
                //Log.i(TAG,"FIle Path : ")
                RVTrackingData trackingData = new RVTrackingData();
                trackingData.setPath(filePath);
                trackingData.setLocation(location);
                trackingData.setEmotion(emotion);

                 trackingDatas.add(trackingData);
            }
        }

        rvTrackingAdapter.setVideoList(trackingDatas);
        recyclerView.setAdapter(rvTrackingAdapter);
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);

        locationServices = LocationServices.getLocationServices(TrackingActivity.this);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                map = mapboxMap;

                // Check if user has granted location permission. If they haven't, we request it
                // otherwise we enable location tracking.
                if (!locationServices.areLocationPermissionsGranted()) {
                    ActivityCompat.requestPermissions(TrackingActivity.this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
                } else {
                    enableLocationTracking();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_VIDEO && resultCode == RESULT_OK){
            try {
                ContentValues addRowValue = new ContentValues();
                String filePath = data.getStringExtra(FILE_PATH);
                String location = data.getStringExtra(LOCATION);
                String emotion = data.getStringExtra(EMOTION);

                Log.i(TAG, "File path : " + filePath + ", Location : " + location + ", Emotion : " + emotion);
                addRowValue.put(FILE_PATH, filePath);
                addRowValue.put(LOCATION, location);
                addRowValue.put(EMOTION, emotion);

                long insertRecordId = dbManager.insert(addRowValue);
                Log.i(TAG, "Insert Record : " + insertRecordId);
                RVTrackingData trackingData = new RVTrackingData();
                trackingData.setPath(filePath);
                trackingData.setLocation(location);
                trackingData.setEmotion(emotion);
                trackingDatas.add(trackingData);
                rvTrackingAdapter.setVideoList(trackingDatas);
            }catch(Exception e){
                Log.i(TAG,e.toString());
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
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

    private void enableLocationTracking() {

        // Disable tracking dismiss on map gesture
        map.getTrackingSettings().setDismissAllTrackingOnGesture(false);

        // Enable location and bearing tracking
        map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationTracking();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_capture:{
                Log.i(TAG,"Clicked");
                Intent intent = new Intent(TrackingActivity.this, CameraActivity.class);
                startActivityForResult(intent,REQUEST_VIDEO);
                break;
            }

            case R.id.btn_clear:{
                dbManager.deleteTable();
                break;
            }
        }
    }
}
