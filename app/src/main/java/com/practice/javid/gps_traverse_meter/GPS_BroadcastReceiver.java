package com.practice.javid.gps_traverse_meter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;


public class GPS_BroadcastReceiver extends BroadcastReceiver {

    private Converter converter;
    private MainActivity mainActivity;

    public GPS_BroadcastReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.converter = new Converter();

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            try {
                boolean GPS_Service_Running = intent.getBooleanExtra("GPS_Service_Running", false);
                mainActivity.setGPS_Service_Running(GPS_Service_Running);

                if (!mainActivity.isStartParamsSetted()) {
                    String startLocationString = intent.getStringExtra("startLocation");
                    Location startLocation = converter.stringToLocation(startLocationString);

                    long startTimeMillis = intent.getLongExtra("startTimeMillis", 0L);
                    mainActivity.setStartTimeMillis(startTimeMillis);

                    String lat = String.valueOf(startLocation.getLatitude());
                    String lon = String.valueOf(startLocation.getLongitude());
                    String alt = String.valueOf(startLocation.getAltitude());
                    String time = mainActivity.getDateString(startTimeMillis);

                    mainActivity.startParamsSetter(lat, lon, alt, time);
                }

                double distance = intent.getDoubleExtra("distance", 0D);
                String currentLocationString = intent.getStringExtra("currentLocation");
                Location currentLocation = converter.stringToLocation(currentLocationString);

                String lat = String.valueOf(currentLocation.getLatitude());
                String lon = String.valueOf(currentLocation.getLongitude());
                String alt = String.valueOf(currentLocation.getAltitude());

                mainActivity.currentParamsSetter(lat, lon, alt, distance);

                if (!mainActivity.isLabelsSetted()) {
                    mainActivity.labelSetter();
                }

            } catch (Exception e) {
                Log.e("My Code", e.getMessage());
            }
        }
    }
}
