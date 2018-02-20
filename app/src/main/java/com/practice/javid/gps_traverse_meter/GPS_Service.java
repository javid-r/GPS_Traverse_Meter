package com.practice.javid.gps_traverse_meter;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Date;

public class GPS_Service extends Service implements LocationListener {

    private Context context;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location startLocation;
    private Location lastLocation;
    private long startTime;
    private double distance;


    private long minTimeUpdates = 0L;

    @Override
    public void onCreate() {
        context = GPS_Service.this;
        locationListener = GPS_Service.this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocation = null;
        distance = 0;
        startTime = System.currentTimeMillis();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (Build.VERSION.SDK_INT >= 23) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdates, 0, locationListener);
                }

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdates, 0, locationListener);
            }
        } catch (Exception e) {
            Log.e("My Code: ", "class -> GPS_Service in method -> onStartCommand -> " + e.toString());
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        Context context = this;
        try {

            if (location == null) {
                return;
            }

            if (startLocation == null){
                startLocation = location;
                lastLocation = location;
            }

            distance += lastLocation.distanceTo(location);
            lastLocation = location;
            Log.e("dist", distance + "");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("Location_Changed");
            broadcastIntent.putExtra("distance", distance);
            broadcastIntent.putExtra("GPS_Service_Running", true);
            broadcastIntent.putExtra("startTimeMillis", startTime);
            broadcastIntent.putExtra("startLocation", startLocation.getLatitude() + "," + startLocation.getLongitude() + "," + startLocation.getAltitude());
            broadcastIntent.putExtra("currentLocation", location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude());

            context.sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            Log.e("My code", e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            Log.e("My Code: ", "class -> GPS_Service in method -> onDestroy -> " + e.toString());
        }
        super.onDestroy();
    }
}
