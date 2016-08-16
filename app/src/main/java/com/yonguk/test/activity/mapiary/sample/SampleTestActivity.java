package com.yonguk.test.activity.mapiary.sample;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;

import java.util.jar.Manifest;

public class SampleTestActivity extends AppCompatActivity implements OnMapReadyCallback{
    private MapView mapView;
    private MapboxMap mapboxMap;
    Button btn,btnCurrent = null;

    LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;
    private static final String TAG = "SampleTestActivity";
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Account manager need to be called before setContentView()
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_sample_test);

        locationServices = LocationServices.getLocationServices(this);

        btn = (Button) findViewById(R.id.test_btn);
        btnCurrent = (Button) findViewById(R.id.test_btn_current);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"clicked");
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(37.5806, 126.8882))
                                .zoom(13)
                                .tilt(20)
                                .build()

                ));
                mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.5806, 126.8882))
                                .title("I am here")
                                .snippet("welcome to my marker")

                );

            }
        });

        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapboxMap != null){
                    toggleGps(!mapboxMap.isMyLocationEnabled());
                }
            }
        });


        mapView=(MapView)findViewById(R.id.test_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
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

    @UiThread
    public void toggleGps(boolean enableGps){
        if(enableGps){
            //Check if user has granted Location permission
            if(!locationServices.areLocationPermissionsGranted()){
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },PERMISSIONS_LOCATION);
            } else{
                enableLocation(true);
            }
        }else{
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled){
        if(enabled){
            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(location != null){
                        //Move the camera to where the user location is
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(location))
                                .zoom(15)
                                .build()
                        );
                    }
                }
            });
            //floatingActionButton.setImageResource(R.drawable.location_disabled);
        }else{
            //floatingActionButton.setImageResource(R.drawable.location);
        }
        mapboxMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    enableLocation(true);
                }
            }
        }
    }
}
