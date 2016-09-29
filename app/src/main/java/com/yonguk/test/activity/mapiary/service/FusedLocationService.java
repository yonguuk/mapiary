package com.yonguk.test.activity.mapiary.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FusedLocationService extends Service implements LocationListener
            , GoogleApiClient.ConnectionCallbacks
            , GoogleApiClient.OnConnectionFailedListener{

    private static final long INTERVAL = 1000*2;
    private static final long FASTEST_INTERVAL = 1000*1;

    private static final String TAG = "FusedLocationService";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    ArrayList<Location> locationList = null;
    String mLastUpdateTime;

    @Override
    public void onCreate() {
        super.onCreate();
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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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

    public FusedLocationService() {
    }

    public void startLocationRecord(){
        locationList = new ArrayList<>();
    }

    public JSONObject getLocationJson(){
        JSONObject locationJson = createJsonObject(locationList);
        locationList = null;
        return locationJson;
    }

    public class LocalBinder extends Binder{
        public FusedLocationService getFusedLocationService(){
            return FusedLocationService.this;
        }
    }

    private final Binder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind()");
        mGoogleApiClient.connect();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind()");
        mGoogleApiClient.disconnect();
        return super.onUnbind(intent);
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
        if(location != null){
            mCurrentLocation = location;
            Log.i(TAG, "lat : " + location.getLatitude() +", " + "lon : " + location.getLongitude());
            if(locationList != null){
                locationList.add(mCurrentLocation);
            }
        }

    }

    private JSONObject createJsonObject(List<Location> coords){
        JSONObject myJsonObject = new JSONObject();
        try {
            JSONObject geometry = new JSONObject();
            geometry.put("type", "LineString");
            JSONArray coordiates = new JSONArray();
            for(int i=0; i<coords.size(); i++){
                JSONArray latLon = new JSONArray();
                latLon.put(coords.get(i).getLatitude());
                latLon.put(coords.get(i).getLongitude());
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
