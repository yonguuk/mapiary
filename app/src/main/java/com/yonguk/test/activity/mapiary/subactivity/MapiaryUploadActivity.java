package com.yonguk.test.activity.mapiary.subactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class MapiaryUploadActivity extends AppCompatActivity {

    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    EditText etTitle, etContent;
    Button btnUpload;
    private String userID = "";
    Context mContext = null;
    private final String TAG = "MapiaryUploadActivity";
    private final String USER_ID = "user_id";
    private final String TITLE = "title";
    private final String CONTENT = "content";
    private final String UPLOAD_URL = "http://kktt0202.dothome.co.kr/master/upload/upload_mapiary.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapiary_upload);

        mContext = this;
        Intent intent = getIntent();
        userID = intent.getStringExtra(USER_ID);
        volleySingleton = VolleySingleton.getInstance(mContext);
        requestQueue = volleySingleton.getRequestQueue();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mapiary 만들기");

        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        volleySingleton = VolleySingleton.getInstance(mContext);
        requestQueue = volleySingleton.getRequestQueue();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
/*                final String user_id = userID;
                final String title = etTitle.getText().toString();
                final String content = etContent.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(USER_ID,user_id);
                intent.putExtra(TITLE,title);
                intent.putExtra(CONTENT, content);
                setResult(RESULT_OK, intent);
                finish();*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

/*    private void upload(){

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        final String user_id = userID;
        final String title = etTitle.getText().toString();
        final String content = etContent.getText().toString();


        StringRequest request = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.i(TAG, response.toString());
                    Log.i(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i(TAG, "error : " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(USER_ID,user_id);
                params.put(TITLE, title);
                params.put(CONTENT,content);
                return params;
            }
        };
        requestQueue.add(request);
    }*/

    private void upload(){
        final String user_id = userID;
        final String title = etTitle.getText().toString();
        final String content = etContent.getText().toString();

        Log.i("MapiaryUploadActivity", user_id + "," +  title + "," +  content);
        StringRequest request = new StringRequest(Request.Method.GET,
                UPLOAD_URL + "?user_id=" + user_id + "&title=" + title + "$content=" + content,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        });
    }

}
