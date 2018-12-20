package com.resonatestudios.uas_1603631_muhammad_nabillah.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.resonatestudios.uas_1603631_muhammad_nabillah.R;
import com.resonatestudios.uas_1603631_muhammad_nabillah.adapter.BrakeLogAdapter;
import com.resonatestudios.uas_1603631_muhammad_nabillah.controller.DbBrakeLog;
import com.resonatestudios.uas_1603631_muhammad_nabillah.model.BrakeLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {
    // location
    private static final int PERMISSION_REQUEST = 99;
    GoogleApiClient googleApiClient;
    RecyclerView recyclerViewLog;
    BrakeLogAdapter brakeLogAdapter;
    TextView test;
    // sensor
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double prevZ;
    double currZ;
    // DB
    DbBrakeLog dbBrakeLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = findViewById(R.id.test);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        prevZ = 0;
        currZ = Double.MAX_VALUE;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        buildGoogleApiClient();

        recyclerViewLog = findViewById(R.id.recycler_view_log);
        brakeLogAdapter = new BrakeLogAdapter(this);

        dbBrakeLog = new DbBrakeLog(this, brakeLogAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // set menu di action bar
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                // klik Clear
                // list akan dikosongkan dan recyclerview akan di-notify
                brakeLogAdapter.getBrakeLogs().clear();
                brakeLogAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_save:
                // klik Save
                // db dikosongkan
                dbBrakeLog.open();
                dbBrakeLog.deleteAll();
                dbBrakeLog.close();

                // list disave ke DB
                ArrayList<BrakeLog> brakeLogs = brakeLogAdapter.getBrakeLogs();
                dbBrakeLog.open();
                int i = 0;
                for (BrakeLog x: brakeLogs) {
                    if (dbBrakeLog.insert(x.getTimestamp(), x.getLatitude(), x.getLongitude())) {
                        i++;
                    }
                }
                dbBrakeLog.close();
                Toast.makeText(this, "Saved " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_load:
                // klik Load
                // kosongkan recycler view
                brakeLogAdapter.getBrakeLogs().clear();
                brakeLogAdapter.notifyDataSetChanged();

                // ambil dari DB
                dbBrakeLog.open();
                ArrayList<BrakeLog> loaded = dbBrakeLog.getAll();
                dbBrakeLog.close();
                brakeLogAdapter.replaceBrakeLogs(loaded);
                brakeLogAdapter.notifyItemInserted(0);
                Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    private void setRecyclerView() {
        recyclerViewLog.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLog.setAdapter(brakeLogAdapter);
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // tampilkan dialog minta izin
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        } else {
            // jika sudah ada izin

            // start sensor and recycler view adapter
            sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            setRecyclerView();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // stop sensor
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start sensor and recycler view adapter
                sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                setRecyclerView();
            } else {
                // jika izin ditolak
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("Tidak mendapat izin, tidak dapat menjalankan aplikasi");
                alertDialog.show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        System.exit(0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            String newText = "X: " + (int)event.values[0] + "\n" +
                    "Y: " + (int)event.values[1] + "\n" +
                    "Z: " + (int)event.values[2];
            test.setText(newText);

            currZ = event.values[2];

            // calculate for emergency brake
            if ((int)currZ <= 0 && (int)prevZ > 3) {
                // bila terdeteksi rem mendadak

                // get location
                double[] latLng = getLocation();
                if (latLng != null) {
                    // insert to adapter's list and refresh
                    brakeLogAdapter.addToList(new BrakeLog(Calendar.getInstance().getTime(), latLng[0], latLng[1]));
                }
            }

            // save prevvalue
            prevZ = currZ;
        }
    }

    public double[] getLocation() {
        // check if user already gave permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if not permitted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);

            return null;
        }

        double[] latLng = new double[2];
        /// get last location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latLng[0] = location.getLatitude();
                latLng[1] = location.getLongitude();
            }
        }).addOnFailureListener(e -> {
            // some log here
        });

        return latLng;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }
}
