package com.yonguk.test.activity.mapiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private final String LOGIN_URL = "http://kktt0202.dothome.co.kr/login/login.php";
    private final String KEY_ID = "user_id";
    private final String KEY_PASSWORD = "password";

    LinearLayout rootLayout = null;
    Context mContext = null;
    EditText etId = null;
    EditText etPassword = null;
    Button btnLogin = null;
    TextView tvSignUpLink = null;

    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setLayout();

        volleySingleton = VolleySingleton.getInstance(mContext);
        requestQueue = volleySingleton.getRequestQueue();
    }

    private void setLayout(){
            mContext = this;
            rootLayout = (LinearLayout) findViewById(R.id.ll_root);
            etId = (EditText) findViewById(R.id.et_id);
            etPassword = (EditText) findViewById(R.id.et_password);
            btnLogin = (Button) findViewById(R.id.btn_login);
            tvSignUpLink = (TextView) findViewById(R.id.tv_signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void login(){
        Log.d(TAG, "Login");
        if(!validate()){
            onLoginFailed();
            return;
        }

        //btnLogin.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(mContext,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String user_id = etId.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(response.trim().equals("success")) {
                            Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
                            Log.i("uks",response.toString());
                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
                            Log.i("uks",response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.i("uks", error.toString());
                        Toast.makeText(mContext, error.toString(),Toast.LENGTH_LONG).show();
                }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_ID,user_id);
                params.put(KEY_PASSWORD,password);
                return params;
            }
        };

        requestQueue.add(request);

   /*     //TODO : Implement your own authentication logic here
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //On complete call either onLoginSuccess or on LoginFailed
                onLoginSuccess();
                //onLoginFailed();
                progressDialog.dismiss();
            }
        }, 1000);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                //TODO : Implement successful signup logic here
                //By default we just finish the Activity and log them in automatically
                //this.finish();
                Snackbar.make(rootLayout, "회원가입이 완료되었습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        btnLogin.setEnabled(true);
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(){
        Toast.makeText(mContext, "Login Failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    public boolean validate(){
        boolean isValid = true;

        String id = etId.getText().toString();
        String password = etPassword.getText().toString();

        return isValid;
    }
}
