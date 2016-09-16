package com.yonguk.test.activity.mapiary.subactivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.camera.CameraHelper;
import com.yonguk.test.activity.mapiary.data.LocationInfo;
import com.yonguk.test.activity.mapiary.utils.CurDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener, LocationListener
            , GoogleApiClient.ConnectionCallbacks
            , GoogleApiClient.OnConnectionFailedListener{

    private Camera mCamera;
    private TextureView mPreview;
    private Button btnCapture;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;

    private boolean isRecording = false;
    private static final String TAG = "RecordActivity";
    private final int REQUEST_CODE_RECORD = 1;

    private static final long INTERVAL = 1000*1;
    private static final long FASTEST_INTERVAL = 1000*1;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    LocationInfo latlon = new LocationInfo();
    List<LocationInfo> locationInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        if(!isGooglePlayServicesAvailable()){
            finish();
        }

        mPreview = (TextureView) findViewById(R.id.texture_view);
        btnCapture = (Button) findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(this);

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient(){
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        createLocationRequest();

    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates(){
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "Location update started ..............: ");
        }catch (SecurityException e){
            Log.d(TAG, e.toString());
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }

    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        Log.d(TAG, "Location update stopped .......................");
    }

/*    private void updateUI(){
        if(mCurrentLocation != null){
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lon = String.valueOf(mCurrentLocation.getLongitude());
            tv.setText("위치정보 가져오는중...");
            tvLocation.setText("At time: " + mLastUpdateTime + "\n"
                    + "위도 : " + lat + "\n"
                    + "경도 : " + lon + "\n"
                    + "정확도 : " + mCurrentLocation.getAccuracy() + "\n"
                    + "Provider: " + mCurrentLocation.getProvider());
        } else{
            Log.d(TAG, "location is null ...............");
        }
    }*/

    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link android.media.MediaRecorder} and {@link android.hardware.Camera}. When not recording,
     * it prepares the {@link android.media.MediaRecorder} and starts recording.
     */
    @Override
    public void onClick(View v) {
        if (isRecording) {
            // BEGIN_INCLUDE(stop_release_media_recorder)

            // stop recording and release camera
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                mOutputFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            btnCapture.setText("Capture");
            isRecording = false;
            releaseCamera();
            // END_INCLUDE(stop_release_media_recorder)
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                    , Uri.parse("file://" + mOutputFile.getPath())));
            String location = createJsonObject(locationInfos).toString();
            Intent intent  = new Intent(RecordActivity.this, UploadActivity.class);
            intent.putExtra("path", mOutputFile.getPath());
            intent.putExtra("location", location);
            startActivity(intent);
            finish();
        } else {

            // BEGIN_INCLUDE(prepare_start_media_recorder)

            new MediaPrepareTask().execute(null, null, null);
            locationInfos = new ArrayList<>();
            // END_INCLUDE(prepare_start_media_recorder)

        }
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder(){

        // BEGIN_INCLUDE (configure_preview)
        mCamera = CameraHelper.getDefaultCameraInstance();
        mCamera.setDisplayOrientation(90);
        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);
        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        // END_INCLUDE (configure_preview)


        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOrientationHint(90);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);
        //mMediaRecorder.setVideoSize(420,420);
        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());
        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        latlon.setLat(mCurrentLocation.getLatitude());
        latlon.setLon(mCurrentLocation.getLongitude());
        DateFormat dateFormat =new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        mLastUpdateTime = dateFormat.format(Calendar.getInstance().getTime());
        if(locationInfos != null){
            locationInfos.add(latlon);
        }
        //updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                RecordActivity.this.finish();
            }
            // inform the user that recording has started
            btnCapture.setText("Stop");

        }
    }


    private JSONObject createJsonObject(List<LocationInfo> coords){
        JSONObject myJsonObject = new JSONObject();
        try {
            JSONObject geometry = new JSONObject();
            geometry.put("type", "LineString");
            JSONArray coordiates = new JSONArray();
            for(int i=0; i<coords.size(); i++){
                JSONArray latLon = new JSONArray();
                latLon.put(coords.get(i).getLat());
                latLon.put(coords.get(i).getLon());
                coordiates.put(latLon);
            }
            geometry.put("coordinates",coordiates);
            myJsonObject.put("geometry",geometry);
        }catch(JSONException e){
            Log.e(TAG,e.toString());
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
        Log.i(TAG,myJsonObject.toString());
        return myJsonObject;
    }


    private boolean isGooglePlayServicesAvailable(){
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS == status){
            Log.d(TAG,"Available");
            return true;
        } else{
            //GooglePlayServicesUtil.getErrorDialog(status,this,0).show();
            GoogleApiAvailability.getInstance().getErrorDialog(this,status,0).show();
            Log.d(TAG,"Not Available");
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()){
            //startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}
