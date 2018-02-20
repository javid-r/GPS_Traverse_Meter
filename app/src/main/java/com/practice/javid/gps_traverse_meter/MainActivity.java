package com.practice.javid.gps_traverse_meter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 1;

    private ToneGenerator ToneGenerator;

    private AppCompatActivity context;
    private TextView distanceLabel;
    public TextView distanceValue;
    private TextView startLatitudeLabel;
    private TextView startLatitudeValue;
    private TextView srartLongitudeLabel;
    private TextView srartLongitudeValue;
    private TextView startAltitudeLabel;
    private TextView startAltitudeValue;
    private TextView currentLatitudeLabel;
    private TextView currentLatitudeValue;
    private TextView currentLongitudeLabel;
    private TextView currentLongitudeValue;
    private TextView currentAltitudeLabel;
    private TextView currentAltitudeValue;
    private TextView startTimeLabel;
    private TextView startTimeValue;
    private TextView totalTimeLabel;
    private TextView totalTimeValue;
    private boolean labelsSetted;
    private boolean startParamsSetted;

    private long startTimeMillis;
    private boolean GPS_Service_Running;
    private Location startLocation;
    private Location currentLocation;

    private ToggleButton controlButton;

    private GPS_BroadcastReceiver onLocationChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        context = MainActivity.this;
        labelsSetted = false;
        startParamsSetted = false;
        GPS_Service_Running = false;
        ToneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 200);

        onLocationChangedReceiver = new GPS_BroadcastReceiver(MainActivity.this);

        IntentFilter LocationChangedIntentFilter = new IntentFilter();
        LocationChangedIntentFilter.addAction("Location_Changed");
        registerReceiver(onLocationChangedReceiver, LocationChangedIntentFilter);

        distanceLabel = (TextView) findViewById(R.id.txt_label_distance);
        distanceValue = (TextView) findViewById(R.id.txt_value_distance);
        startLatitudeLabel = (TextView) findViewById(R.id.txt_label_start_lat);
        startLatitudeValue = (TextView) findViewById(R.id.txt_value_start_lat);
        srartLongitudeLabel = (TextView) findViewById(R.id.txt_label_start_lon);
        srartLongitudeValue = (TextView) findViewById(R.id.txt_value_start_lon);
        startAltitudeLabel = (TextView) findViewById(R.id.txt_label_start_alt);
        startAltitudeValue = (TextView) findViewById(R.id.txt_value_start_alt);
        currentLatitudeLabel = (TextView) findViewById(R.id.txt_label_current_lat);
        currentLatitudeValue = (TextView) findViewById(R.id.txt_value_current_lat);
        currentLongitudeLabel = (TextView) findViewById(R.id.txt_label_current_lon);
        currentLongitudeValue = (TextView) findViewById(R.id.txt_value_current_lon);
        currentAltitudeLabel = (TextView) findViewById(R.id.txt_label_current_alt);
        currentAltitudeValue = (TextView) findViewById(R.id.txt_value_current_alt);
        startTimeLabel = (TextView) findViewById(R.id.txt_label_start_time);
        startTimeValue = (TextView) findViewById(R.id.txt_value_start_time);
        totalTimeLabel = (TextView) findViewById(R.id.txt_label_total_time);
        totalTimeValue = (TextView) findViewById(R.id.txt_value_total_time);

        controlButton = (ToggleButton) findViewById(R.id.btn_control);

        controlButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startGPS_Service();
                } else {
                    stopGPS_Service();
                    setStartTimeMillis(0);
                    setGPS_Service_Running(false);
                    startParamsSetted = false;
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == permissionRequestCode) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(context, GPS_Service.class));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onLocationChangedReceiver);
        super.onDestroy();
    }

    public void makeBeep() {
        ToneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 280);
    }

    private void stopGPS_Service() {
        stopService(new Intent(context, GPS_Service.class));
    }

    private void startGPS_Service() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(context, GPS_Service.class));
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            }
        } else {
            startService(new Intent(context, GPS_Service.class));
        }
    }

    public boolean isLabelsSetted() {
        return labelsSetted;
    }

    public void labelSetter() {
        labelsSetted = true;
        distanceLabel.setText(getString(R.string.distance));
        startLatitudeLabel.setText(getString(R.string.start_lat));
        srartLongitudeLabel.setText(getString(R.string.start_lon));
        startAltitudeLabel.setText(getString(R.string.start_alt));
        currentLatitudeLabel.setText(getString(R.string.current_lat));
        currentLongitudeLabel.setText(getString(R.string.current_lon));
        currentAltitudeLabel.setText(getString(R.string.current_alt));
        startTimeLabel.setText(getString(R.string.start_time));
        totalTimeLabel.setText(getString(R.string.total_time));
    }

    public boolean isStartParamsSetted() {
        return startParamsSetted;
    }

    public void startParamsSetter(String startLat, String startLon, String startAlt, String startTime) {
        startParamsSetted = true;
        startLatitudeValue.setText(startLat);
        srartLongitudeValue.setText(startLon);
        startAltitudeValue.setText(startAlt);
        startTimeValue.setText(startTime);
    }

    public void currentParamsSetter(String currentLat, String currentLon, String currentAlt, double distance) {
        currentLatitudeValue.setText(currentLat);
        currentLongitudeValue.setText(currentLon);
        currentAltitudeValue.setText(currentAlt);
        setTotalTimeValue(getStartTimeMillis());
        distanceValue.setText(String.valueOf(distance));
    }

    public boolean isGPS_Service_Running() {
        return GPS_Service_Running;
    }

    public void setGPS_Service_Running(boolean GPS_Service_Running) {
        this.GPS_Service_Running = GPS_Service_Running;
        this.controlButton.setChecked(GPS_Service_Running);
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDateString(long startTimeMillis){
        long dateLong = new Date(startTimeMillis).getTime();
        java.text.DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(context);
        return dateFormatter.format(dateLong);
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStartTimeMillis() {
        return this.startTimeMillis;
    }

    public void setTotalTimeValue(long startTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long totalTimeMillis = currentTimeMillis - startTimeMillis;

        long millis = totalTimeMillis % 1000;
        long seconds = (totalTimeMillis / 1000) % 60;
        long minutes = (totalTimeMillis / (60 * 1000)) % 60;
        long hours = (totalTimeMillis / (60 * 60 * 1000)) % 24;
        long days = totalTimeMillis / (24 * 60 * 60 * 1000);

        String totalTimeString;
        if (days > 0) {
            String format = "%dD %02d:%02d:%02d:%03d";
            totalTimeString = String.format(format, days, hours, minutes, seconds, millis);
        } else {
            String format = "%02d:%02d:%02d:%03d";
            totalTimeString = String.format(format, hours, minutes, seconds, millis);
        }

        totalTimeValue.setText(totalTimeString);
    }
}
