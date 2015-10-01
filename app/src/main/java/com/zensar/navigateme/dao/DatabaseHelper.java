package com.zensar.navigateme.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zensar.navigateme.dto.Location;

import java.util.ArrayList;

/**
 * Created by ry41071 on 04-09-2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "navigateme.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LOCATION = "location";
    private static final String KEY_ID = "id";
    private static final String KEY_LOCATION_NAME = "locationname";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_DATE_TIME = "datetime";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_MILLIS = "millis";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_Z = "z";

    private static DatabaseHelper mDatabaseHelper = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(context);
        return mDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LOCATION_NAME + " TEXT,"
                + KEY_SPEED + " TEXT," + KEY_DATE_TIME + " TEXT," + KEY_LAT + " TEXT," + KEY_LONG + " TEXT," + KEY_MILLIS + " TEXT," + KEY_X + " TEXT," + KEY_Y + " TEXT," + KEY_Z + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locationList = new ArrayList<Location>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION + "  ORDER BY id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setLocationName(cursor.getString(1));
                location.setSpeed(cursor.getString(2));
                location.setDateTime(cursor.getString(3));
                location.setLatitude(cursor.getString(4));
                location.setLongitude(cursor.getString(5));
                location.setMillis(cursor.getString(6));
                location.setX(cursor.getString(7));
                location.setY(cursor.getString(8));
                location.setZ(cursor.getString(9));

                locationList.add(location);
            } while (cursor.moveToNext());
        }
        Log.d("dao", locationList.size() + "");
        return locationList;
    }

    public boolean setLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, location.getLocationName());
        values.put(KEY_SPEED, location.getSpeed());
        values.put(KEY_DATE_TIME, location.getDateTime());
        values.put(KEY_LAT, location.getLatitude());
        values.put(KEY_LONG, location.getLongitude());
        values.put(KEY_MILLIS, location.getMillis());
        values.put(KEY_X, location.getX());
        values.put(KEY_Y, location.getY());
        values.put(KEY_Z, location.getZ());

        db.insert(TABLE_LOCATION, null, values);
        db.close();
        Log.d("dao", location.getLatitude());
        Log.d("dao", location.getLongitude());
        Log.d("dao", "successfull");
        return true;
    }

    public Location getlastLocation() {
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION + " WHERE id = (SELECT MAX(id)  FROM " + TABLE_LOCATION + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Location location = new Location();
        if (cursor.moveToFirst()) {
            location.setLocationName(cursor.getString(1));
            location.setSpeed(cursor.getString(2));
            location.setDateTime(cursor.getString(3));
            location.setLatitude(cursor.getString(4));
            location.setLongitude(cursor.getString(5));
            location.setMillis(cursor.getString(6));
            location.setX(cursor.getString(7));
            location.setY(cursor.getString(8));
            location.setZ(cursor.getString(9));
        }
        return location;
    }
}
