package com.yonguk.test.activity.mapiary.subactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.camera.Upload;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv;
    String path;
    private Button btnUpload;
    private TextView tvPath, tvUrl;
    Bitmap thumbnail;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    final String UPLOAD_IMAGE_URL = "http://kktt0202.dothome.co.kr/image/upload_preview.php";
    private static final String KEY_IMAGE = "image";
    private static final String TAG = "UploadActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        volleySingleton = VolleySingleton.getInstance(this);
        requestQueue = volleySingleton.getRequestQueue();
        btnUpload = (Button) findViewById(R.id.btn_upload);
        tvPath = (TextView) findViewById(R.id.tv_path);
        tvUrl = (TextView) findViewById(R.id.tv_url);
        iv = (ImageView) findViewById(R.id.iv);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        thumbnail = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MINI_KIND);
        iv.setImageBitmap(thumbnail);

        tvPath.setText(path);
        btnUpload.setOnClickListener(this);
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
                uploading = ProgressDialog.show(UploadActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                tvUrl.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload uv = new Upload();
                uploadImage();
                String msg = uv.uploadVideo(path);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void uploadImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
}
