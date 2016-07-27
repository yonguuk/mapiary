package com.yonguk.test.activity.mapiary;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SampleChildActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnChoose, btnUpload = null;
    ImageView imageView = null;
    EditText editText = null;
    Bitmap bitmap = null;
    Context context = null;
    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;
    final int PICK_IMAGE_REQUEST = 1;
    final String UPLOAD_URL = "http://kktt0202.dothome.co.kr/image/upload/upload.php";
    final String KEY_IMAGE = "image";
    final String KEY_NAME = "name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Child Activity");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        btnChoose = (Button) findViewById(R.id.btn_choose);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        editText = (EditText) findViewById(R.id.et);
        imageView = (ImageView) findViewById(R.id.iv);

        volleySingleton = VolleySingleton.getInstance(context);
        requestQueue = volleySingleton.getRequestQueue();

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_choose:
                showFileChooser();
                break;

            case R.id.btn_upload:
                uploadImage();
                break;
        }
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //convert bitmap to base64 String
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...", "Please wait...", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Dismissing the progress dialog
                loading.dismiss();
                //Showing Toast message of the response
                Toast.makeText(context, response.toString(),Toast.LENGTH_LONG).show();
                Log.i("uks",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(context, error.toString(),Toast.LENGTH_LONG).show();
                Log.i("uks",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = editText.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new HashMap<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST){
            if(resultCode == RESULT_OK && data != null && data.getData() != null){
                Uri filePath = data.getData();
                try{
                    //Getting bitmap from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                    //Setting the Bitmaq to ImageView
                    imageView.setImageBitmap(bitmap);
                }catch(IOException e){
                    Log.i("uks", e.getMessage());
                }catch(Exception e){
                    Log.i("uks", e.getMessage());
                }
            }
        }
    }


}
