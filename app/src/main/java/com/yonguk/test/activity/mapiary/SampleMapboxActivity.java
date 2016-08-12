package com.yonguk.test.activity.mapiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class SampleMapboxActivity extends AppCompatActivity {
    private MapView mapView;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_mapbox);

        try {
            // Mapbox access token only needs to be configured once in your app
            MapboxAccountManager.start(this, ACCESS_TOKEN);

            // This contains the MapView in XML and needs to be called after the account manager
            setContentView(R.layout.activity_sample_mapbox);

            mapView = (MapView) findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {

                    // Customize map with markers, polylines, etc.

                }
            });
        }catch (Exception e){
            Log.i("uks",e.getMessage());
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
}
