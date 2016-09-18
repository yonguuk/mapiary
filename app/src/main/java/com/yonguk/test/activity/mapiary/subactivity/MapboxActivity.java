package com.yonguk.test.activity.mapiary.subactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.PolylineUtils;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapboxActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;

    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    private JSONObject resultJson=null;
    private int emotion;
    private String emotionColor;
    private final int EMOTION_NUTRAL = 0;
    private final int EMOTION_RELAX = 1;
    private final int EMOTION_ACTIVE = 2;
    private final int EMOTION_STRESS = 3;
    private static final String TAG = "MapboxActivity";
    private final String KEY_LOCATION = "location";
    private final String KEY_EMOTION = "emotion";
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_mapbox);

        volleySingleton = VolleySingleton.getInstance(this);
        requestQueue = volleySingleton.getRequestQueue();
        Intent intent = getIntent();
        getJSONFromUrl(intent.getStringExtra(KEY_LOCATION));
        emotion = intent.getIntExtra(KEY_EMOTION,1);
        emotionColor = getEmotionColor(emotion);
        mapView=(MapView)findViewById(R.id.test_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        new DrawGeoJSON().execute();
    }

    /**Draw GeoJson Line**/
    private class DrawGeoJSON extends AsyncTask<Void, Void, List<Position>> {

        @Override
        protected List<Position> doInBackground(Void... voids) {
            ArrayList<Position> points = parseJson(resultJson);
            //tvLocation.setText(getAddress(points.get(0).getLatitude(),points.get(0).getLongitude()));
            return points;
        }

        @Override
        protected void onPostExecute(List<Position> points) {
            super.onPostExecute(points);
            drawSimplify(points);
            Log.i(TAG, "onPostExecute()");
        }
    }


    public void getJSONFromUrl(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,"성공");
                Log.i(TAG,response.toString());
                resultJson = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"실패");
                Log.i(TAG,error.toString());
                resultJson = null;
            }
        });
        requestQueue.add(request);
    }

    private ArrayList<Position> parseJson(JSONObject jsonLocation){
        ArrayList<Position> points = new ArrayList<>();

        try{
            JSONObject json = jsonLocation;
            //JSONArray features = json.getJSONArray("features");
            //JSONObject feature = features.getJSONObject(0);
            JSONObject geometry = json.getJSONObject("geometry");

            if(geometry != null){
                String type = geometry.getString("type");
                if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")){
                    JSONArray coords = geometry.getJSONArray("coordinates");
                    for(int i=0; i<coords.length(); i++){
                        JSONArray coord = coords.getJSONArray(i);
                        Position latLng = Position.fromCoordinates(coord.getDouble(1),coord.getDouble(0));
                        points.add(latLng);
                        Log.i(TAG, "아" + points.get(i).getLatitude() + " , " +  points.get(i).getLongitude());
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG,"Excepting Loading GeoJson: " + e.toString());
        }
        return points;
    }


    private void drawSimplify(List<Position> points) {

        Position[] before = new Position[points.size()];
        for (int i = 0; i < points.size(); i++) before[i] = points.get(i);

        Position[] after = PolylineUtils.simplify(before, 0.001);

        LatLng[] result = new LatLng[after.length];
        for (int i = 0; i < after.length; i++)
            result[i] = new LatLng(after[i].getLatitude(), after[i].getLongitude());
        //address = getAddress(points.get(0).getLatitude(),points.get(0).getLongitude());
        //tvLocation.setText(address);
        mapboxMap.addPolyline(new PolylineOptions()
                .add(result)
                .color(Color.parseColor(emotionColor))
                .width(6));

        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(result[0])
                .zoom(18)
                .tilt(20)
                .build()

        ));
        mapboxMap.addMarker(new MarkerOptions()
                .position(result[0])
                .title("Hello World!")
                .snippet("Welcome to my marker."));


/*
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(result[0])
                .zoom(14)
                .tilt(20)
                .build()

        ));
*/


    }


    private String getEmotionColor(int emotion){
        String color;
        if(emotion == EMOTION_RELAX){
            color = "#4CAF50";
        }else if(emotion == EMOTION_ACTIVE){
            color = "#9C27B0";
        }else if(emotion == EMOTION_STRESS){
            color = "#F44336";
        }else{
            color = "#9E9E9E";
        }
        return color;
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
