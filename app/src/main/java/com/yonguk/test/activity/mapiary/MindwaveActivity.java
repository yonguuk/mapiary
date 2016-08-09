package com.yonguk.test.activity.mapiary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.neurosky.connection.*;
import com.neurosky.connection.DataType.MindDataType;
//import com.neurosky.connection.TgStreamHandler;
//import com.neurosky.connection.TgStreamReader;

public class MindwaveActivity extends AppCompatActivity implements View.OnClickListener {

    private TgStreamReader tgStreamReader = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private String adress = null;
    private int badPacketCount = 0;

    TextView tvAttention,tvMeditation,tvHighAlpha,tvLowAlpha,tvHighBeta,tvLowBeta, tvRow = null;
    Button btnStart, btnStop, btnSelectDevice = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindwave);

        setView();

        try{
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"Please enable your bluetooth!", Toast.LENGTH_LONG).show();
                finish();
            }
        }catch(Exception e){
            Log.i("MindWaveActivity", e.getMessage());
            return;
        }

        tgStreamReader = new TgStreamReader(mBluetoothAdapter,callback);
        tgStreamReader.setGetDataTimeOutTime(6);
    }

    private void setView(){
        tvAttention = (TextView) findViewById(R.id.tv_attention);
        tvMeditation = (TextView) findViewById(R.id.tv_meditation);
        tvHighAlpha = (TextView) findViewById(R.id.tv_high_alpha);
        tvLowAlpha = (TextView) findViewById(R.id.tv_low_alpha);
        tvHighBeta = (TextView) findViewById(R.id.tv_high_beta);
        tvLowBeta = (TextView) findViewById(R.id.tv_low_beta);
        tvRow = (TextView) findViewById(R.id.tv_row);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnSelectDevice = (Button) findViewById(R.id.btn_selectdevice);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnSelectDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:{
                badPacketCount = 0;
                if(tgStreamReader != null && tgStreamReader.isBTConnected()){
                    tgStreamReader.stop();
                    tgStreamReader.close();
                }

                tgStreamReader.connect();
                //tgStreamReader.connectAndStart();
                break;
            }

            case R.id.btn_stop:{
                tgStreamReader.stop();
                tgStreamReader.close();
                break;
            }

            case R.id.btn_selectdevice:{

                break;
            }
        }
    }

    private TgStreamHandler callback = new TgStreamHandler() {
        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onStatesChanged(int connectionState) {
            Log.d("MindwaveActivity", "Connection State Changed to : " + connectionState);
            switch(connectionState){
                case ConnectionStates.STATE_CONNECTING:
                    break;

                case ConnectionStates.STATE_CONNECTED:
                    tgStreamReader.start();
                    //Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    break;

                case ConnectionStates.STATE_WORKING:
                    tgStreamReader.startRecordRawData();
                    break;

                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    tgStreamReader.stopRecordRawData();
                    //Toast.makeText(getApplicationContext(),"data time out", Toast.LENGTH_LONG).show();
                    break;

                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.

                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionState;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
            badPacketCount ++;
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_BAD_PACKET;
            msg.arg1 = badPacketCount;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e("MindwaveActivigty","onRecordFail: " +flag);
        }
    };


    private boolean isPressing = false;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;
    int raw;

    private Handler LinkDetectedHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MindDataType.CODE_RAW:
                    tvRow.setText("row: " + msg.arg1);
                    break;

                case MindDataType.CODE_MEDITATION:
                    Log.d("MindwaveActivity","Code meditation" + msg.arg1);
                    tvMeditation.setText("Meditation: " + msg.arg1);
                    break;

                case MindDataType.CODE_ATTENTION:
                    Log.d("MindwaveActivity", "CODE_ATTENTION " + msg.arg1);
                    tvAttention.setText("Attention" +msg.arg1 );
                    break;

                case MindDataType.CODE_EEGPOWER:
                    EEGPower power = (EEGPower)msg.obj;
                    if(power.isValidate()){
                        tvHighAlpha.setText("high alpha: " + power.highAlpha);
                        tvLowAlpha.setText("low alpha: " + power.lowAlpha);
                        tvHighBeta.setText("high beta: " + power.highBeta);
                        tvLowBeta.setText("low beta: " + power.lowBeta);
                    }
                    break;

                case MindDataType.CODE_POOR_SIGNAL:
                    break;
                case MSG_UPDATE_BAD_PACKET:
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void stop(){
        if(tgStreamReader != null){
            tgStreamReader.stop();
            tgStreamReader.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();

    }

    @Override
    protected void onDestroy() {
        if(tgStreamReader != null){
            tgStreamReader.close();
            tgStreamReader = null;
        }
        super.onDestroy();
    }
}
