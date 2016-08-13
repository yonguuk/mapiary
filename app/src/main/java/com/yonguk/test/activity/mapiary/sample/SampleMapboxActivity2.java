package com.yonguk.test.activity.mapiary.sample;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SampleMapboxActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    MapboxMap mapboxMap = null;
    TextView tvLat,tvLon,tvAddress = null;
    Button btn = null;
    LocationManager locationManager= null;
    JSONObject resultJson = null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    final String URL_SERVER= "http://kktt0202.dothome.co.kr/master/location/location.json";

    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_mapbox2);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = volleySingleton.getRequestQueue();

        tvLat = (TextView)findViewById(R.id.lat);
        tvLon = (TextView) findViewById(R.id.lon);
        tvAddress = (TextView) findViewById(R.id.address);
        btn = (Button)findViewById(R.id.btn);

        MapboxAccountManager.start(this, ACCESS_TOKEN);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new DrawGeoJSON().execute();
            }
        });

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        new DrawGeoJSON().execute();
    }


    private class DrawGeoJSON extends AsyncTask<Void, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            ArrayList<LatLng> points = new ArrayList<>();
            getJSONFromUrl(URL_SERVER);
            try{
                JSONObject json = resultJson;
                JSONArray features = json.getJSONArray("features");
                JSONObject feature = features.getJSONObject(0);
                JSONObject geometry = feature.getJSONObject("geometry");

                if(geometry != null){
                    String type = geometry.getString("type");
                    if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")){
                        JSONArray coords = geometry.getJSONArray("coordinates");
                        for(int i=0; i<coords.length(); i++){
                            JSONArray coord = coords.getJSONArray(i);
                            LatLng latLng = new LatLng(coord.getDouble(1),coord.getDouble(0));
                            points.add(latLng);
                        }
                    }
                }
            }catch(Exception e){
                Log.e("uks","Excepting Loading GeoJson: " + e.toString());
            }
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);

            if(points.size()>0){
                LatLng[] pointArray = points.toArray(new LatLng[points.size()]);
                mapboxMap.addPolyline(new PolylineOptions()
                                .add(pointArray)
                                .color(Color.parseColor("#3bb2d0"))
                                .width(2)
                );
            }
        }
    }


    public void getJSONFromUrl(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("uks", "성공");
                Log.i("uks",response.toString());
                resultJson = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","실패");
                Log.i("uks",error.toString());
            }
        });
        requestQueue.add(request);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView.onPause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
}
