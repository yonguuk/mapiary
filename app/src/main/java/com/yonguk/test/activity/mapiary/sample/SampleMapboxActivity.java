package com.yonguk.test.activity.mapiary.sample;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SampleMapboxActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback{
    //private MapView mapView;
    MapboxMap mapboxMap = null;
    TextView tvLat,tvLon,tvAddress, tvState = null;
    Button btnStart,btn,btnMark = null;
    LocationManager locationManager= null;
    JSONObject resultJson = null;
    List<LatLng> locationData = null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    final String URL_SERVER= "http://kktt0202.dothome.co.kr/master/location/location.json";
    final String URL_UPLOAD= "http://kktt0202.dothome.co.kr/master/location/location.php";
    double lat=0;
    double lon=0;

    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_mapbox);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = volleySingleton.getRequestQueue();

        tvLat = (TextView)findViewById(R.id.lat);
        tvLon = (TextView) findViewById(R.id.lon);
        tvAddress = (TextView) findViewById(R.id.address);
        tvState = (TextView) findViewById(R.id.state);
        btnStart = (Button) findViewById(R.id.btn_start);
        btn = (Button)findViewById(R.id.btn);
        btnMark = (Button) findViewById(R.id.mark);

        /**gps**/
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        registerLocationUpdates();
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        //Create supportMapFragment
        SupportMapFragment mapFragment;
        if(savedInstanceState == null){
            //create fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //LatLng patagonia = new LatLng(37.5806, 126.8884);
            LatLng patagonia = new LatLng(45.5076, -122.6736);
            //Build mapboxMap
            MapboxMapOptions options = new MapboxMapOptions();
            options.styleUrl("mapbox://styles/mapbox/streets-v9");
            options.camera(new CameraPosition.Builder()
                            .target(patagonia)
                            .zoom(12)
                            .build()
            );
            getJSONFromUrl(URL_SERVER);
            mapFragment = SupportMapFragment.newInstance(options);
            transaction.add(R.id.mapbox_container, mapFragment,"com.mapbox.map");
            transaction.commit();
        }else{
            mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
        }


        mapFragment.getMapAsync(this);



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("uks", "btn clicked");
                locationData = new ArrayList<LatLng>();
                tvState.setText("측정시작");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //JSONObject obj = createJsonObject(locationData);
                Log.i("uks","created json : " + resultJson.toString());

                tvState.setText("업로드");
                sendJsonRequest(resultJson);
            }
        });

        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private JSONObject createJsonObject(List<LatLng> coords){
        JSONObject myJsonObject = new JSONObject();
        try {
            JSONObject geometry = new JSONObject();
            geometry.put("type", "LineString");
            JSONArray coordiates = new JSONArray();
            JSONArray latLon = new JSONArray();
            for(int i=0; i<coords.size(); i++){
                latLon.put(coords.get(i).getLatitude());
                latLon.put(coords.get(i).getLongitude());
                coordiates.put(latLon);
            }
            geometry.put("coordinates",coordiates);
            myJsonObject.put("geometry",geometry);
        }catch(JSONException e){
            Log.e("uks",e.toString());
        }catch (Exception e){
            Log.e("uks",e.toString());
        }
        return myJsonObject;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        new DrawGeoJSON().execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.i("uks", "onLocationChanaged() called");
        //Toast.makeText(getApplicationContext(),"위치정보가 갱신되었습니다", Toast.LENGTH_SHORT).show();
        lat = location.getLatitude();
        lon = location.getLongitude();
        LatLng latLng = new LatLng(lat,lon);
        tvLat.setText("위도 : " + lat);
        tvLon.setText("경도 : " + lon);
        tvAddress.setText("주소 : " + getAddress(lat, lon));
        if(locationData != null){
            if(locationData.size()<60) {
                locationData.add(latLng);
                if(locationData.size() == 60){
                    tvState.setText("측정완료");
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


/*
   private void sendJsonRequest(final JSONObject obj) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_UPLOAD,obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("uks","uploadjson : " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","uploadjson : " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               Map<String,String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        requestQueue.add(request);
    }
*/



    private void sendJsonRequest(final JSONObject obj){
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("uks","uploadjson : " + response.toString());
                Log.i("uks","업로드성공");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("uks","uploadjson : " + error.getMessage());
                Log.i("uks","업로드실패");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("location", obj.toString());
                return params;
            }
        };
        requestQueue.add(request);
    }
    private class DrawGeoJSON extends AsyncTask<Void, Void, List<LatLng>>{

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            ArrayList<LatLng> points = new ArrayList<>();

            try{
                JSONObject json = resultJson;
                //JSONArray features = json.getJSONArray("features");
                //JSONObject feature = features.getJSONObject(0);
                JSONObject geometry = json.getJSONObject("geometry");

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
                                .width(4)
                );
            }
        }
    }

    public void getJSONFromUrl(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("uks","성공");
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






    private void registerLocationUpdates(){
        //1000은 1초마다, 1은 1미터마다 해당 값을 갱신한다는 뜻으로, 딜레이마다 호출하기도 하지만
        //위치값을 판별하여 일정 미터단위 움직임이 발생 했을 때에도 리스너를 호출 할 수 있다.
        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
        registerLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
        try{
            locationManager.removeUpdates(this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }

    /** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
    public String getAddress(double lat, double lng){
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        try{
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(list == null){
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }

        if(list.size() > 0){
            Address addr = list.get(0);
            address = addr.getCountryName() + " "
                    //+ addr.getPostalCode() + " "
                    + addr.getAdminArea() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare() + " "
                    + addr.getFeatureName();
        }

        return address;



    }

}
