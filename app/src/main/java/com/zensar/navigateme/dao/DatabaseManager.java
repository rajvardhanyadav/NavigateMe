package com.zensar.navigateme.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zensar.navigateme.dto.Location;

import java.util.ArrayList;

/**
 * Created by ry41071 on 04-09-2015.
 */
public class DatabaseManager {
    Context mContext;
    protected SQLiteDatabase mDatabase = null;
    private DatabaseHelper mDbHelper = null;

    public DatabaseManager(Context context) {
        mContext = context;
        mDbHelper = DatabaseHelper.getHelper(mContext);
        open();
    }

    public void open() throws SQLException {
        if (mDbHelper == null) {
            mDbHelper = DatabaseHelper.getHelper(mContext);
        }
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public ArrayList<Location> getLocations() {
        return mDbHelper.getAllLocations();
    }

    public boolean setLocation(Location location) {
        return mDbHelper.setLocation(location);
    }

    public Location getlastLocation(){
        return mDbHelper.getlastLocation();
    }
}
