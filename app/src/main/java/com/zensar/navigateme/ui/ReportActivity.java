package com.zensar.navigateme.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.zensar.navigateme.adapter.MyAdapter;
import com.zensar.navigateme.dao.DatabaseManager;
import com.zensar.navigateme.service.MyLocationService;
import com.zensar.navigateme.R;

import java.util.ArrayList;
import java.util.Set;

public class ReportActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    ListView listView;

    DatabaseManager databaseManager;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Context context = this;
        databaseManager = new DatabaseManager(this);
        SharedPreferences sharedPref = context.getSharedPreferences("Locations", Context.MODE_PRIVATE);

        //listView = (ListView) findViewById(R.id.listView);

        ArrayList<com.zensar.navigateme.dto.Location> tempList = databaseManager.getLocations();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(tempList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
