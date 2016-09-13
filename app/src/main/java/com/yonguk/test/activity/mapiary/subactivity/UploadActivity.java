package com.yonguk.test.activity.mapiary.subactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.provider.MediaStore;
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
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.camera.Upload;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    ImageView iv;
    String path;
    LinearLayout root;
    private Toolbar toolbar;
    //private Button btnUpload;
    //private TextView tvPath, tvUrl;
    private MapView mapView;
    private MapboxMap mapboxMap;
    Bitmap thumbnail;
    JSONObject resultJson=null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    final String UPLOAD_IMAGE_URL = "http://kktt0202.dothome.co.kr/master/upload/upload_preview.php";
    final String URL_LOCATION= "http://kktt0202.dothome.co.kr/master/location/test.json";

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
        getJSONFromUrl(URL_LOCATION);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
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
                uploadVideo();
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
                uploadImage(s);
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

                params.put(KEY_IMAGE, image);
                params.put("card_id", cardId);
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
    }

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
                        .color(Color.parseColor("#cc0000"))
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_upload){
            uploadVideo();
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
        mapView = (MapView) findViewById(R.id.mapview);
        //btnUpload = (Button) findViewById(R.id.btn_upload);
        //tvPath = (TextView) findViewById(R.id.tv_path);
        //tvUrl = (TextView) findViewById(R.id.tv_url);
        iv = (ImageView) findViewById(R.id.iv);
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
