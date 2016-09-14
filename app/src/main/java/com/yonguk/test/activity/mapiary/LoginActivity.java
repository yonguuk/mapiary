package com.yonguk.test.activity.mapiary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private final String LOGIN_URL = "http://kktt0202.dothome.co.kr/master/user/login.php";
    private final String KEY_ID = "user_id";
    private final String KEY_PASSWORD = "password";
    LinearLayout rootView = null;
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


    private void login(){
        Log.d(TAG, "Login");
        final String user_id = etId.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if(!validate(user_id, password)){
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("uks",response.toString());
                        progressDialog.dismiss();
                        if(response.trim().equals("success")) {
                            Intent intent = new Intent(mContext, MainTabActivity.class);
                            intent.putExtra("USER_ID", user_id);
                            startActivity(intent);
                            finish();
                        }else if(response.trim().equals("failure")){
                            Log.i("uks",response.toString());
                            Snackbar.make(rootView, "ID 또는 비밀번호가 일치하지 않습니다", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.i("uks", error.toString());
                        Snackbar.make(rootView, "서버와 연결에 실패했습니다", Snackbar.LENGTH_LONG).show();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                //TODO : Implement successful signup logic here
                //By default we just finish the Activity and log them in automatically
                //this.finish();
                Snackbar.make(rootView, "회원가입이 완료되었습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                etId.setText(data.getStringExtra("USER_ID"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        //disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public boolean validate(String user_id, String password){
        if(!user_id.matches("[A-Za-z0-9]+")){
            Snackbar.make(rootView, "ID는 영문과 숫자만 사용 할 수 있습니다.",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(user_id.length()<4 || user_id.length()>10){
            Snackbar.make(rootView,"ID는 4자리 이상 10자리 이하 입니다.",Snackbar.LENGTH_LONG).show();
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
        rootView = (LinearLayout) findViewById(R.id.login_root_view);
        etId = (EditText) findViewById(R.id.et_id);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvSignUpLink = (TextView) findViewById(R.id.tv_signup);


        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                login();
            }
        });

        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }



    private void hideKeyboard(){
        ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

/*    private void showKeyborad(){
        ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }*/
}
