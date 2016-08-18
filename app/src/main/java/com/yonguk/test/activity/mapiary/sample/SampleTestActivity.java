package com.yonguk.test.activity.mapiary.sample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;

public class SampleTestActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener{
    private MapView mapView;
    private MapboxMap mapboxMap;
    Button btn,btnCurrent, btnGeoline, btnData = null;
    TextView tvLat,tvLon,tvAddress, tvDatasize = null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;

    LocationServices locationServices;
    List<LatLng> locationData = null;
    double lat=0;
    double lon=0;
    String address = "";
    JSONObject resultJson=null;
    private static final int PERMISSIONS_LOCATION = 0;
    private static final String TAG = "SampleTestActivity";
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    final String URL_LOCATION= "http://kktt0202.dothome.co.kr/master/location/test.json";
    private final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/location/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Account manager need to be called before setContentView()
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_sample_test);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = volleySingleton.getRequestQueue();
        getJSONFromUrl(URL_LOCATION);
        locationServices = LocationServices.getLocationServices(this);
        locationServices.addLocationListener(this);

        tvLat = (TextView) findViewById(R.id.test_tv_lat);
        tvLon = (TextView) findViewById(R.id.test_tv_lon);
        tvAddress = (TextView)findViewById(R.id.test_tv_address);
        tvDatasize = (TextView) findViewById(R.id.test_tv_datasize);
        btn = (Button) findViewById(R.id.test_btn);
        btnCurrent = (Button) findViewById(R.id.test_btn_current);
        btnGeoline = (Button) findViewById(R.id.test_btn_geoline);
        btnData = (Button) findViewById(R.id.test_btn_data);
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
                    //toggleGps(!mapboxMap.isMyLocationEnabled());
                    enableLocation(true);
                }
            }
        });

        btnGeoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultJson != null){
                    new DrawGeoJSON().execute();
                }
            }
        });

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationData = new ArrayList<LatLng>();
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
    /**My Location**/
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
        LatLng latLng = new LatLng();
        latLng.setLatitude(lat);
        latLng.setLongitude(lon);
        if(enabled){
            if(latLng.getLatitude()!=0 && latLng.getLatitude()!=0) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(latLng))
                                .zoom(15)
                                .build()
                );
            }
        }
        mapboxMap.setMyLocationEnabled(enabled);
    }

/*
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
*/

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

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            Log.i(TAG, "onLocationChanged()");
            lat = location.getLatitude();
            lon = location.getLongitude();
            address = getAddress(lat, lon);

            tvLat.setText("위도 : " + lat);
            tvLon.setText("경도 : " + lon);
            tvAddress.setText("주소" + address);
            LatLng latLng = new LatLng(location);
            if (locationData != null) {
                if (locationData.size() < 60) {
                    locationData.add(latLng);
                    tvDatasize.setText("data size : " + locationData.size());
                    if (locationData.size() == 60) {
                        ArrayList<LatLng> temp = (ArrayList<LatLng>)locationData;
                        locationData = null;
                        writeToFile(createJsonObject(temp));
                    }
                }
            }
        }
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

    /**End My Location**/


    /**Draw GeoJson Line**/
    private class DrawGeoJSON extends AsyncTask<Void, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            ArrayList<LatLng> points = parseJson(resultJson);
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
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(points.get(0))
                                .zoom(13)
                                .tilt(20)
                                .build()

                ));
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
                resultJson = null;
            }
        });
        requestQueue.add(request);
    }

    private ArrayList<LatLng> parseJson(JSONObject jsonLocation){
        ArrayList<LatLng> points = new ArrayList<>();

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
            Log.e("TAG",e.toString());
        }catch (Exception e){
            Log.e("TAG",e.toString());
        }
        return myJsonObject;
    }


    /**End Draw GeoJson Line**/

    /**Write Json File**/
    private void writeToFile(final JSONObject json){
        Thread thread = new Thread("file write"){
            @Override
            public void run() {
                try{
                    DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
                    Calendar cal = Calendar.getInstance();
                    String dateString = dateFormat.format(cal.getTime()) + ".txt";
                    File file = new File(path + dateString);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(json.toString().getBytes());
                    fos.close();
                    Log.d(TAG,"file write complete");
                    tvDatasize.setText("file write complete");
                }catch (Exception e){
                    Log.i(TAG,e.getMessage());
                }
            }
        };
        thread.start();
    }
    /**End Write Json File**/

}
