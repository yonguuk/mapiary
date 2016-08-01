package com.yonguk.test.activity.mapiary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private final String SIGN_UP_URL = "http://kktt0202.dothome.co.kr/master/user/sign_up.php";
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


    private void signUp(){
        Log.d(TAG, "SignUp");

        //TODO : Implement your own signup logic here
        final String user_id = etId.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String passwordConfirm = etConfirm.getText().toString().trim();

        if(!validate(user_id, password, passwordConfirm)){
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, SIGN_UP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(response.trim().equals("success")){
                            Intent intent = new Intent();
                            intent.putExtra("USER_ID",user_id);
                            setResult(RESULT_OK,intent);
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
                if(error.toString().equals("duplication")){
                    Snackbar.make(rootView,"이미 존재하는 ID 입니다",Snackbar.LENGTH_LONG).show();
                } else{
                    Snackbar.make(rootView,error.toString(),Snackbar.LENGTH_LONG).show();
                    Log.i("uks", error.toString());
                }

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
    }

    public boolean validate(String user_id, String password, String passwordConfirm){

        if(!user_id.matches("[A-Za-z0-9]+")){
            Snackbar.make(rootView, "ID는 영문과 숫자만 사용 할 수 있습니다.",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(user_id.length()<4 || user_id.length()>10){
            Snackbar.make(rootView,"ID는 4자리 이상 10자리 이하 입니다.",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(!password.equals(passwordConfirm)){
            Snackbar.make(rootView,"비밀번호가 일치하지 않습니다",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            Snackbar.make(rootView, "비밀번호엔 한글이 포함될 수 없습니다.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        else if(password.length()<4 || password.length()>10){
            Snackbar.make(rootView, "비밀번호는 4자리 이상 10자리 이하 입니다", Snackbar.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void setLayout(){
        mContext = this;
        rootView = (LinearLayout) findViewById(R.id.signup_root_view);
        etConfirm = (EditText) findViewById(R.id.et_confirm_signup);
        etId = (EditText) findViewById(R.id.et_id_signup);
        etPassword = (EditText) findViewById(R.id.et_password_signup);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        tvLoginLink = (TextView) findViewById(R.id.tv_login);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                signUp();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                finish();
            }
        });
    }

    private void hideKeyboard(){
        ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }


}
