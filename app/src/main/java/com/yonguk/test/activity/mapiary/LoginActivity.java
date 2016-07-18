package com.yonguk.test.activity.mapiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    Context mContext = null;
    EditText etId = null;
    EditText etPassword = null;
    Button btnLogin = null;
    TextView tvSignUpLink = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setLayout();
    }

    private void setLayout(){
        mContext = this;
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

        btnLogin.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(mContext,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String id = etId.getText().toString();
        String password = etPassword.getText().toString();


        //TODO : Implement your own authentication logic here
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //On complete call either onLoginSuccess or on LoginFailed
                onLoginSuccess();
                //onLoginFailed();
                progressDialog.dismiss();
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                //TODO : Implement successful signup logic here
                //By default we just finish the Activity and log them in automatically
                this.finish();
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
