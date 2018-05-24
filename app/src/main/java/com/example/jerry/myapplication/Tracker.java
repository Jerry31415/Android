package com.example.jerry.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.widget.Toast;

public class Tracker {

    public LocationManager locationManager;
    public Location loc;
    protected LocationListener LL;

    boolean is_ready;
    int start_code;

    boolean gps_enabled;
    boolean network_enabled;
    Context mContext;

    public boolean isLocationEnabled() {
        return gps_enabled || network_enabled;
    }

    static boolean isFineLocationPermissionGaranted(Activity activity){
        return (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);
    }

    static boolean isCoarseLocationPermissionGaranted(Activity activity){
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);
    }

    static boolean lsLocationEnabled(Activity activity){
        LocationManager LM = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = LM.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean net = LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return (gps || net);
    }

    private void ShowMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    static int SendRequestPermissions(Activity activity){
        int request_code=0;
        String [] perm = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(activity, perm, request_code);
        return request_code;
    }

    public Tracker (Context context){
        start_code = -5;
        is_ready = false;
        mContext = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void updateStatus(){
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public int start() {
        updateStatus();
        if (!gps_enabled && !network_enabled) return 1;
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) return 1;
        LL = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!is_ready) {
                    ShowMessage("Местоположение определено");
                    is_ready = true;
                }
                loc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                updateStatus();
                Location lastKnownLocation = null;
                if(network_enabled){
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                else if(gps_enabled){
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (lastKnownLocation != null) {
                    loc = lastKnownLocation;
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateStatus();
            }
        };
        if(network_enabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, LL);
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else if(gps_enabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, LL);
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return 0;
    }

}
