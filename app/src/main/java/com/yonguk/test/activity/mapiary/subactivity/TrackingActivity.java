package com.yonguk.test.activity.mapiary.subactivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.EEGPower;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.adapter.RVTrackingAdapter;
import com.yonguk.test.activity.mapiary.data.RVTrackingData;
import com.yonguk.test.activity.mapiary.service.FusedLocationService;
import com.yonguk.test.activity.mapiary.utils.DBManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private MapView mapView;
    private MapboxMap map;
    private RecyclerView recyclerView;
    private RVTrackingAdapter rvTrackingAdapter;
    private LocationServices locationServices;
    private final String ACCESS_TOKEN = "pk.eyJ1IjoieW9uZ3VrIiwiYSI6ImNpcnBtYXE4eDAwOXBocG5oZjVrM3Q0MGQifQ.BjzIAl6Kcsdn3KYdtjk26g";
    private String userID ="";
    private List<RVTrackingData> trackingDatas;
    private static final int PERMISSIONS_LOCATION = 0;
    private static final int REQUEST_VIDEO = 10;
    private static final String TAG = "TrackingActivity";
    private final String KEY_ID = "user_id";

    FusedLocationService fusedLocationService = null;

    /** MindWave **/
    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<Double> referList = new ArrayList<>();
    private ArrayList<Double> countList = new ArrayList<>();
    private ArrayList<Integer> emotion = new ArrayList<>();
    private double median_value = 0.68;
    private double count_value;
    private double result_value;
    private int badPacketCount = 0;
    private int referenceValue;
    private int countValue;
    private int result_emotion;
    private String EMOTION_STATE="1";
    ProgressDialog progressDialog ;
    ProgressDialog progressDialog2 ;

    /** SQLITE**/
    DBManager dbManager;
    Button btnCapture;
    Button btnClear;
    static final String FILE_PATH = "file_path";
    static final String LOCATION = "location";
    static final String EMOTION = "emotion";
    static final String DATETIME = "datetime";
    private final String[] columns = new String[]{FILE_PATH, LOCATION, EMOTION};

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            fusedLocationService = ((FusedLocationService.LocalBinder)service).getFusedLocationService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected()");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_tracking);
        context = this;
        progressDialog = new ProgressDialog(context);
        progressDialog2 = new ProgressDialog(context);
        trackingDatas = new ArrayList<>();
        Intent intent = getIntent();
        userID = intent.getStringExtra(KEY_ID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = DBManager.getInstance(context);

        Intent serviceIntent = new Intent(this, FusedLocationService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);

        //==========================================================================================
        try {
            // TODO
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
                finish();
//				return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "error:" + e.getMessage());
            return;
        }
        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tgStreamReader = new TgStreamReader(mBluetoothAdapter,callback);
        // (2) Demo of setGetDataTimeOutTime, the default time is 5s, please call it before connect() of connectAndStart()
        tgStreamReader.setGetDataTimeOutTime(6);
        // (3) Demo of startLog, you will get more sdk log by logcat if you call this function
        tgStreamReader.startLog();

        // (5) demo of isBTConnected
        if(tgStreamReader != null && tgStreamReader.isBTConnected()){
            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // (4) Demo of  using connect() and start() to replace connectAndStart(),
        // please call start() when the state is changed to STATE_CONNECTED
        tgStreamReader.connect();

        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("뇌파 장비와 연결중 입니다....");
        progressDialog.setMessage("잠시만 기다려 주세요...");
        //progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
//				tgStreamReader.connectAndStart();
        //=========================================================================================

/*        Button btn = (Button) findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clicked");
                fusedLocationService.startLocationRecord();
                Intent intent = new Intent(TrackingActivity.this, CameraActivity.class);
                startActivityForResult(intent,REQUEST_VIDEO);
            }
        });*/
        recyclerView = (RecyclerView) findViewById(R.id.rv_tracking);
        rvTrackingAdapter = new RVTrackingAdapter(this, userID);
        Cursor c = dbManager.query(columns, null, null, null, null, null);
        if(c !=null){
            while(c.moveToNext()){
                String filePath = c.getString(0);
                String location = c.getString(1);
                String emotion = c.getString(2);
                //Log.i(TAG,"FIle Path : ")
                RVTrackingData trackingData = new RVTrackingData();
                trackingData.setPath(filePath);
                trackingData.setLocation(location);
                trackingData.setEmotion(emotion);

                 trackingDatas.add(trackingData);
            }
        }

        rvTrackingAdapter.setVideoList(trackingDatas);
        recyclerView.setAdapter(rvTrackingAdapter);
        final GridLayoutManager manager = new GridLayoutManager(this, 2){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        recyclerView.setLayoutManager(manager);

        locationServices = LocationServices.getLocationServices(TrackingActivity.this);

        /** Mapbox **/
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                map = mapboxMap;

                // Check if user has granted location permission. If they haven't, we request it
                // otherwise we enable location tracking.
                if (!locationServices.areLocationPermissionsGranted()) {
                    ActivityCompat.requestPermissions(TrackingActivity.this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
                } else {
                    enableLocationTracking();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_VIDEO && resultCode == RESULT_OK){
            try {
                JSONObject locationJson = fusedLocationService.getLocationJson();
                ContentValues addRowValue = new ContentValues();
                String filePath = data.getStringExtra(FILE_PATH);
                //String location = data.getStringExtra(LOCATION);
                String location = locationJson.toString();
                String emotion = data.getStringExtra(EMOTION);

                Log.i(TAG, "File path : " + filePath + ", Location : " + location + ", Emotion : " + emotion);
                addRowValue.put(FILE_PATH, filePath);
                addRowValue.put(LOCATION, location);
                addRowValue.put(EMOTION, emotion);

                long insertRecordId = dbManager.insert(addRowValue);
                Log.i(TAG, "Insert Record : " + insertRecordId);
                RVTrackingData trackingData = new RVTrackingData();
                trackingData.setPath(filePath);
                trackingData.setLocation(location);
                trackingData.setEmotion(emotion);
                trackingDatas.add(trackingData);
                rvTrackingAdapter.setVideoList(trackingDatas);
            }catch(Exception e){
                Log.i(TAG,e.toString());
            }

        }
    }

    // (7) demo of TgStreamHandler
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            //Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting

                    Log.i(TAG,"State Connecting");
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    Log.i(TAG,"State Connected");
                    tgStreamReader.start();
                    //showToast("Connected", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working

                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it.
                    //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData
                    Log.i(TAG,"State Working");
                    tgStreamReader.startRecordRawData();
                    progressDialog.dismiss();
                    //Toast.makeText(context,"감정 분석을 시작합니다",Toast.LENGTH_LONG).show();
                    //progressDialog.dismiss();

                    //progressDialog.setTitle("감정 분석 중입니다...");

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout
                    Log.i(TAG,"State Get Data Time Out");
                    //(9) demo of recording raw data, exception handling
                    tgStreamReader.stopRecordRawData();

                    //showToast("Get data time out!", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.
                    Log.i(TAG,"Stoped");
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    Log.i(TAG,"StateDiconnected");
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    Log.i(TAG,"State Error");
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    Log.i(TAG,"State Failed");
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionStates;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e(TAG,"onRecordFail: " +flag);

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
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.

            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);

            //Log.i(TAG,"onDataReceived");
        }
    };

    private boolean isPressing = false;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // (8) demo of MindDataType
            switch (msg.what) {
                case MindDataType.CODE_EEGPOWER:
                    EEGPower power = (EEGPower)msg.obj;
                    if(power.isValidate()){
                        //tv_lowalpha.setText("low alpha :"+power.lowAlpha);
                        //tv_highalpha.setText("high alpha :" +power.highAlpha);
                        //tv_lowbeta.setText("low beta :" +power.lowBeta);
                        //tv_highbeta.setText("high beta :" +power.highBeta);

                        double alpha = power.lowAlpha + power.highAlpha;
                        double beta = power.lowBeta + power.highBeta;
                        double rate = beta / alpha;
                        Log.i(TAG,rate+"");
                        //getMedian(rate);
                        getCount(rate);

                        //median.setText("refer median value :" + median_value);
                        //Log.d("median",getMedian(rate)+"");

                        result_value = ((count_value / median_value) - 1) * 100;
                        if (result_value > 0 && result_value < 30 ){
                            //result.setText("result value : 안정 상태" + result_value);
                            result_emotion = 1;
                        }

                        else if (result_value < 60 && result_value > 30) {
                            // result.setText("result value : 흥분 상태" + result_value);
                            result_emotion = 2;
                        }
                        else if (result_value >= 60) {
                            //result.setText("result value : 스트레스 상태" + result_value);
                            result_emotion = 3;
                        }

                        getEmotion(result_emotion);
                        //result.setText("result value : " +EMOTION);
                        //    median.setText("레퍼런스 측정중입니다." );

                        // Log.d("count result", getCount(rate)+"");

                        //tv_rate.setText("beta / alpha :"+ (rate));
                    }
                    break;
                case MindDataType.CODE_RAW:
                    //tv_ps.setText("raw Signal :"+ (msg.arg1));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void getMedian(double value){
        median_value = -1;
        if(referList.size() == 10){
            Collections.sort(referList);
            median_value = referList.get(referList.size()/2);
            return;
        }
        referList.add(value);
    };

    public void getCount(double value){
        count_value = -1;
        if(countList.size() != 10){
            countList.add(value);
        }

        if(countList.size() == 10) {
            for(int i = 0; i < countList.size(); i++)
                count_value += countList.get(i);
            count_value = count_value / 10;
            Log.d("sum",count_value+"");
            countList.remove(0);
        }
    };

    int cnt1, cnt2, cnt3;
    public void getEmotion(int value){
        emotion.add(result_emotion);

        if (emotion.size() == 10) {
            for(int i =0;i < emotion.size(); i++){
                switch (emotion.get(i)){
                    case 1 : cnt1++; break;
                    case 2 : cnt2++; break;
                    case 3 : cnt3++; break;
                }
            }
            emotion.clear();
            if(cnt1 > cnt2 && cnt1 > cnt3) {
                //EMOTION_STATE = "안정";
                EMOTION_STATE = "1";
            }

            else if(cnt2 > cnt1 && cnt2 > cnt3) {
                //EMOTION_STATE = "흥분";
                EMOTION_STATE = "2";
            }

            else if(cnt3> cnt1 && cnt3 > cnt2) {
                //EMOTION_STATE = "스트레스";
                EMOTION_STATE = "3";
            }

            //progressDialog.dismiss();
            if(EMOTION_STATE.equals("1"))
                Toast.makeText(getApplicationContext(), "안정 상태 입니다", Toast.LENGTH_LONG).show();
            if(EMOTION_STATE.equals("2"))
                Toast.makeText(getApplicationContext(), "흥분 상태 입니다", Toast.LENGTH_LONG).show();
            if(EMOTION_STATE.equals("3"))
                Toast.makeText(getApplicationContext(), "스트레스 상태 입니다", Toast.LENGTH_LONG).show();
            fusedLocationService.startLocationRecord();
            Intent intent = new Intent(TrackingActivity.this, CameraActivity.class);
            intent.putExtra(EMOTION, EMOTION_STATE);
            startActivityForResult(intent,REQUEST_VIDEO);
            cnt1 = 0;
            cnt2 = 0;
            cnt3 = 0;
        }
    }


    public void stop() {
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Intent serviceIntent = new Intent(TrackingActivity.this, FusedLocationService.class);
        startService(serviceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Intent serviceIntent = new Intent(TrackingActivity.this, FusedLocationService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        if(tgStreamReader != null){
            tgStreamReader.close();
            tgStreamReader = null;
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void enableLocationTracking() {

        // Disable tracking dismiss on map gesture
        map.getTrackingSettings().setDismissAllTrackingOnGesture(false);

        // Enable location and bearing tracking
        map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationTracking();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

        }
    }
}
