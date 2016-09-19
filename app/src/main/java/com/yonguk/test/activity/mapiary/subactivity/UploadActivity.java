package com.yonguk.test.activity.mapiary.subactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.PolylineUtils;
import com.yonguk.test.activity.mapiary.MainActivity;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.camera.Upload;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    ImageView iv;
    LinearLayout root;
    EditText etText;
    private Toolbar toolbar;
    TextView tvLocation;
    //private Button btnUpload;
    //private TextView tvPath, tvUrl;
    private MapView mapView;
    private MapboxMap mapboxMap;
    Bitmap thumbnail;
    JSONObject resultJson=null;
    String locationJsonString="";
    JSONObject json;

    private String userID;
    private String path;
    private String address;
    private int emotion;
    private String emotionColor;
    private final String KEY_ID = "user_id";
    private final String KEY_PATH = "path";
    private final String KEY_LOCATION = "location";
    private final String KEY_CARD_ID = "card_id";
    private final String KEY_TEXT ="content";
    private final String KEY_EMOTION = "emotion";
    private final String KEY_ADDRESS = "address";
    private final int EMOTION_NUTRAL = 0;
    private final int EMOTION_RELAX = 1;
    private final int EMOTION_ACTIVE = 2;
    private final int EMOTION_STRESS = 3;
    String textContent;


    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    //final String UPLOAD_IMAGE_URL = "http://kktt0202.dothome.co.kr/master/upload/upload_preview.php";
    final String UPLOAD_IMAGE_URL = "http://kktt0202.dothome.co.kr/master/upload/upload_address.php";
    final String URL_LOCATION= "http://kktt0202.dothome.co.kr/master/location/location3.json";

    //ArrayList<LatLng> points;
    ArrayList<Position> points;

    private static final String KEY_IMAGE = "image";
    private static final String TAG = "UploadActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_upload);
        volleySingleton = VolleySingleton.getInstance(this);
        requestQueue = volleySingleton.getRequestQueue();

        setView();
        //getJSONFromUrl(URL_LOCATION);
        Intent intent = getIntent();
        userID = intent.getStringExtra(KEY_ID);
        path = intent.getStringExtra(KEY_PATH);
        locationJsonString= intent.getStringExtra(KEY_LOCATION);
        emotion = intent.getIntExtra(KEY_EMOTION,1);
        emotionColor = getEmotionColor(emotion);
        Log.i(TAG, locationJsonString);
        try {
            json = new JSONObject(locationJsonString);
        }catch (JSONException e){
            Log.d(TAG,e.toString());
        }
        //points = parseJson(json);
        //Log.i(TAG,"lat: " + points.get(0).getLatitude()+ "," +"lon : " + points.get(0).getLongitude());
        thumbnail = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MINI_KIND);
        iv.setImageBitmap(thumbnail);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //tvPath.setText(path);
        //btnUpload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_upload:
                //uploadVideo();
                uploadImage("1");
                break;
        }
    }

    private void uploadVideo(){
        class UploadVideo extends AsyncTask<Void, Void, String> {
            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadActivity.this, "Uploading...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                // uploadImage(s);
                //tvUrl.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                // tvUrl.setText(Html.fromHtml(s));
                //uploadImage(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload uv = new Upload();
                String msg = uv.uploadVideo(path);
                Log.i("CARDID : " , msg);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void uploadImage(final String cardId){
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.i(TAG, response.toString());
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.i(TAG, error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(thumbnail);

    /*            //Getting Image Name
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                String emotion = "happy";*/
                Map<String, String> params = new HashMap<>();
                textContent = etText.getText().toString();
                params.put(KEY_ID,userID);
                params.put(KEY_IMAGE, image);
                //params.put(KEY_CARD_ID, cardId);
                params.put(KEY_LOCATION, json.toString());
                params.put(KEY_TEXT,textContent);
                params.put(KEY_EMOTION,emotion+"");
                params.put(KEY_ADDRESS,address);
                Log.i(TAG, userID + "," + cardId + "," + textContent + "," + emotion);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        // Load and Draw the GeoJSON
        new DrawGeoJSON().execute();
/*
                mapboxMap.addMarker(new MarkerOptions()
                        .position(points.get(0))
                        .title("Hello World!")
                        .snippet("Welcome to my marker."));



        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(points.get(0))
                .zoom(13)
                .tilt(20)
                .build()
        ));
            */
    }

    /**Draw GeoJson Line**/
    private class DrawGeoJSON extends AsyncTask<Void, Void, List<Position>> {

        @Override
        protected List<Position> doInBackground(Void... voids) {
            ArrayList<Position> points = parseJson(json);
            //tvLocation.setText(getAddress(points.get(0).getLatitude(),points.get(0).getLongitude()));
            return points;
        }

        @Override
        protected void onPostExecute(List<Position> points) {
            super.onPostExecute(points);

/*
            if(points.size()>0){
                LatLng[] pointArray = points.toArray(new LatLng[points.size()]);
                mapboxMap.addPolyline(new PolylineOptions()
                        .add(pointArray)
                        .color(Color.parseColor("#cc0000"))
                        .width(4)
                );

                mapboxMap.addMarker(new MarkerOptions()
                        .position(points.get(0))
                        .title("Hello World!")
                        .snippet("Welcome to my marker."));

                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(pointArray[0])
                        .zoom(14)
                        .tilt(20)
                        .build()

                ));
            }


*/
            drawSimplify(points);
            Log.i(TAG, "onPostExecute()");
        }
    }

    private void drawSimplify(List<Position> points) {

        Position[] before = new Position[points.size()];
        for (int i = 0; i < points.size(); i++) before[i] = points.get(i);

        Position[] after = PolylineUtils.simplify(before, 0.001);

        LatLng[] result = new LatLng[after.length];
        for (int i = 0; i < after.length; i++)
            result[i] = new LatLng(after[i].getLatitude(), after[i].getLongitude());
        address = getAddress(points.get(0).getLatitude(),points.get(0).getLongitude());
        tvLocation.setText(address);
        mapboxMap.addPolyline(new PolylineOptions()
                .add(result)
                .color(Color.parseColor(emotionColor))
                .width(6));

        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(result[0])
                .zoom(19)
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



/*

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


*/




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
            address = //addr.getCountryName() + " "
                    //+ addr.getPostalCode() + " "
                     addr.getAdminArea() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare();
                    //+ addr.getFeatureName();
        }

        return address;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_upload){
            uploadImage("1");
        }

        if(id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    private void setView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("올리기");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        root = (LinearLayout) findViewById(R.id.root);
        etText = (EditText) findViewById(R.id.et_text);
        mapView = (MapView) findViewById(R.id.mapview);
        tvLocation = (TextView)findViewById(R.id.tv_location);
        //btnUpload = (Button) findViewById(R.id.btn_upload);
        //tvPath = (TextView) findViewById(R.id.tv_path);
        //tvUrl = (TextView) findViewById(R.id.tv_url);
        iv = (ImageView) findViewById(R.id.iv);
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
        mapView.onDestroy();
    }


}