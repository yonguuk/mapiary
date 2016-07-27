package com.yonguk.test.activity.mapiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SignUpActivity";
    private final String SIGN_UP_URL = "http://kktt0202.dothome.co.kr/login/sign_up.php";
    private final String KEY_ID = "user_id";
    private final String KEY_PASSWORD = "password";
    Context mContext = null;
    EditText etConfirm = null;
    EditText etId = null;
    EditText etPassword = null;
    Button btnSignUp = null;
    TextView tvLoginLink = null;
    LinearLayout rootView = null;


    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setLayout();

        volleySingleton = VolleySingleton.getInstance(mContext);
        requestQueue = volleySingleton.getRequestQueue();
    }

    private void setLayout(){
        rootView = (LinearLayout) findViewById(R.id.signup_root_view);
        mContext = this;
        etConfirm = (EditText) findViewById(R.id.et_confirm_signup);
        etId = (EditText) findViewById(R.id.et_id_signup);
        etPassword = (EditText) findViewById(R.id.et_password_signup);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        tvLoginLink = (TextView) findViewById(R.id.tv_login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void signUp(){
        Log.d(TAG, "SignUp");

        if(!validate()){
            onSignUpFailed();
            return;
        }

        //btnSignUp.setEnabled(false);






        //TODO : Implement your own signup logic here
        final String user_id = etId.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String passwordConfirm = etConfirm.getText().toString().trim();

        if(!password.equals(passwordConfirm)){
            Snackbar.make(rootView,"confirm your password",Snackbar.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(mContext,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SIGN_UP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(response.trim().equals("Success")){
                            //Snackbar.make(rootView,"sign up success",Snackbar.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Snackbar.make(rootView,response.toString(),Snackbar.LENGTH_LONG).show();
                            Log.i("uks", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("uks", "error : " + error.toString());
                Snackbar.make(rootView,"실패하였습니다",Snackbar.LENGTH_LONG).show();
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



        //SignUpRequest s = new SignUpRequest();
        //s.execute(id,name,password);



/*        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //On complete call either onSignupsuccess or onsignupfailed
                //depending on success
                onSignUpSuccess();
                progressDialog.dismiss();
            }
        },0);*/


    }

    private void onSignUpSuccess(){
        btnSignUp.setEnabled(true);
        setResult(RESULT_OK,null);
        finish();
    }

    private void onSignUpFailed(){
        Toast.makeText(mContext, "Login failed",Toast.LENGTH_LONG).show();
        btnSignUp.setEnabled(true);
    }

    public boolean validate(){
        boolean isValid = true;
        return isValid;
    }



}
