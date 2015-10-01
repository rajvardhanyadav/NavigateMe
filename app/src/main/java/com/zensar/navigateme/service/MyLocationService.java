package com.zensar.navigateme.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.zensar.navigateme.dao.DatabaseManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyLocationService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private DatabaseManager databaseManager;
    private float speed;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
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
                location1 = new com.zensar.navigateme.dto.Location(addressString, speedKmperHrs, formattedDate, currentLatitude + "", currentLongitude + "", c.getTimeInMillis() + "");
            } else {
                location1 = new com.zensar.navigateme.dto.Location(addressString, "0", formattedDate, currentLatitude + "", currentLongitude + "", c.getTimeInMillis() + "");
            }
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

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
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

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}