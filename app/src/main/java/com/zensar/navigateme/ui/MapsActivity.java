package com.zensar.navigateme.ui;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zensar.navigateme.R;
import com.zensar.navigateme.dao.DatabaseManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    //    public GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    //    public LocationRequest mLocationRequest;
    private TextView textViewSpeed, textViewX, textViewY, textViewZ, textViewLat, textViewLong;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor mAccelerometer;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private DatabaseManager databaseManager;
    float x, y, z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //startService(new Intent(MapsActivity.this, MyLocationService.class));

        setUpMapIfNeeded();

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)
                .setFastestInterval(1 * 1000);*/

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);
        textViewZ = (TextView) findViewById(R.id.textViewZ);
        textViewLat = (TextView) findViewById(R.id.textViewLat);
        textViewLong = (TextView) findViewById(R.id.textViewLong);

        textViewSpeed.setText("Speed : 0.0 km/hr");

        initializeLocationManager();
        databaseManager = new DatabaseManager(this);
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    }

    private void handleNewLocation(Location location) {
        Log.d("NavigateMe", location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        textViewLat.setText("Latitude : " + currentLatitude);
        textViewLong.setText("Longitude : " + currentLongitude);
        String addr = getAddress(currentLatitude, currentLongitude);
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(addr);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private String getAddress(double currentLatitude, double currentLongitude) {
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        String addressString = "";
        try {

            List<Address> addresses = gc.getFromLocation(currentLatitude, currentLongitude, 1);
            StringBuilder sb = new StringBuilder();

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");

                sb.append(address.getCountryName());
            }
            addressString = sb.toString();
        } catch (IOException e) {
        }
        return addressString;
    }

    public String calculateDistance(Location locationA, Location locationB, String lastMillis, Long currentMillis) {
        float distance = locationA.distanceTo(locationB);
        Log.d("distance", "Location A" + locationA.getLatitude() + " " + locationA.getLongitude());
        Log.d("distance", "Location B" + locationB.getLatitude() + " " + locationB.getLongitude());
        Log.d("distance", "Meters : " + distance + "");

        float timeHop = Long.valueOf(currentMillis) - Long.valueOf(lastMillis);
        Log.d("distance", "timeHop : " + timeHop + "");

        double distanceInKm = distance / 1000;
        Log.d("distance", "distanceInKm : " + distanceInKm + "");
        double timeInHrs = (timeHop / 3600000);
        Log.d("distance", "timeInHrs : " + timeInHrs + "");
        double speed = distanceInKm / timeInHrs;

        Log.d("distance", "km/hr : " + speed + "");
        String speedKmperHrs = round(speed, 2) + "";
        Log.d("distance", "km/hr str: " + speedKmperHrs + "");
        return speedKmperHrs;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            handleNewLocation(location);
            mLastLocation.set(location);

            Log.d("speed", location.getSpeed() + "");

            Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
            String addressString = "";
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            try {
                List<Address> addresses = gc.getFromLocation(currentLatitude, currentLongitude, 1);
                StringBuilder sb = new StringBuilder();

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (IOException e) {
            }
            Log.e(TAG, "Address: " + addressString);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            com.zensar.navigateme.dto.Location lastLocation = databaseManager.getlastLocation();
            com.zensar.navigateme.dto.Location location1;
            if (lastLocation.getLongitude() != null && lastLocation.getLatitude() != null) {
                Location locationA = new Location("point A");
                locationA.setLatitude(currentLatitude);
                locationA.setLongitude(currentLongitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(Double.valueOf(lastLocation.getLatitude()));
                locationB.setLongitude(Double.valueOf(lastLocation.getLongitude()));
                String speedKmperHrs = calculateDistance(locationA, locationB, lastLocation.getMillis(), c.getTimeInMillis());
                textViewSpeed.setText("Speed : " + speedKmperHrs + " km/hr");
                location1 = new com.zensar.navigateme.dto.Location(addressString, speedKmperHrs, formattedDate, currentLatitude + "", currentLongitude + "", c.getTimeInMillis() + "");
            } else {
                location1 = new com.zensar.navigateme.dto.Location(addressString, "0", formattedDate, currentLatitude + "", currentLongitude + "", c.getTimeInMillis() + "");
            }
            location1.setX(x + "");
            location1.setY(y + "");
            location1.setZ(z + "");
            databaseManager.setLocation(location1);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reports:
                startActivity(new Intent(MapsActivity.this, ReportActivity.class));
                return true;
            case R.id.action_export:
                startActivity(new Intent(MapsActivity.this, ExportActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        Log.d("sensor", "x : " + x);
        Log.d("sensor", "y : " + y);
        Log.d("sensor", "z : " + z);

        textViewX.setText("X : " + x);
        textViewY.setText("Y : " + y);
        textViewZ.setText("Z : " + z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
