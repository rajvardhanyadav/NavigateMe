package com.zensar.navigateme.dto;

/**
 * Created by ry41071 on 04-09-2015.
 */
public class Location {
    String locationName;
    String speed;
    String dateTime;
    String latitude;
    String longitude;
    String x;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    String y;
    String z;

    public String getMillis() {
        return millis;
    }

    public void setMillis(String millis) {
        this.millis = millis;
    }

    String millis;

    public Location() {

    }

    public Location(String locationName, String speed, String dateTime, String latitude, String longitude, String millis) {
        this.locationName = locationName;
        this.speed = speed;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.millis = millis;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
