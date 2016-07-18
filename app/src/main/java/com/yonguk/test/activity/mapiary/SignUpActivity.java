package com.yonguk.test.activity.mapiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SignUpActivity";

    Context mContext = null;
    EditText etName = null;
    EditText etId = null;
    EditText etPassword = null;
    Button btnSignUp = null;
    TextView tvLoginLink = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setLayout();
    }

    private void setLayout(){
        mContext = this;
        etName = (EditText) findViewById(R.id.et_name_signup);
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

        btnSignUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(mContext,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        //TODO : Implement your own signup logic here
        String name = etName.getText().toString();
        String id = etId.getText().toString();
        String password = etPassword.getText().toString();

        //SignUpRequest s = new SignUpRequest();
        //s.execute(id,name,password);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //On complete call either onSignupsuccess or onsignupfailed
                //depending on success
                onSignUpSuccess();
                progressDialog.dismiss();
            }
        }, 3000);
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

    /*
    private class SignUpRequest extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... voids) {
            try {
                //URL url = new URL("http://kktt0202.dothome.co.kr/sign_up.php?id=" + voids[0] +"&name=" + voids[1] + "&password=" + voids[2]);
                URL url = new URL("http://kktt0202.dothome.co.kr/sign_up.php?id=why&name=so&password=serious");
                Log.d("uks",url.toString());
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                if(con == null){
                    Log.d("uks","널입니다");
                }

            }catch(Exception e){
                Log.d("urlException", e.getMessage());
            }
            return null;
        }
    }
    */


}
