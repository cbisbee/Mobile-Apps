package com.csci405.hikeshare;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Charles on 12/11/2017.
 */

public class HikeLocationListener implements LocationListener {
    GeoPoint currentLocation;
    ArrayList<GeoPoint> pointCollection;
    boolean currentlyHiking;
    double minLocationUpdateDistance;

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    private double distanceInMilesBetweenPoints(GeoPoint p1, GeoPoint p2) {
        double earthRadiusMi = 3959;
        double lat1 = 0,lat2 = 0,lon1 = 0,lon2 = 0;
        lat1 = p1.getLatitude();
        lat2 = p2.getLatitude();
        lon1 = p1.getLongitude();
        lon2 = p2.getLongitude();

        double dLat = degreesToRadians(lat2-lat1);
        double dLon = degreesToRadians(lon2-lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadiusMi * c;
    }

    public HikeLocationListener(){
        pointCollection = new ArrayList<>();
        currentlyHiking = false;
        minLocationUpdateDistance = .01;
    }
    public void onLocationChanged(Location location) {
        if(distanceInMilesBetweenPoints(currentLocation,new GeoPoint(location)) >= minLocationUpdateDistance){
            if(currentlyHiking){
                currentLocation = new GeoPoint(location);
                pointCollection.add(currentLocation);
            }
        }
    }

    public void onProviderDisabled(String provider) {
        //Something needs to go here
    }

    public void onProviderEnabled(String provider) {
        //Something needs to go here
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Something needs to go here
    }

    public void setCurrentlyHiking(boolean val){
        currentlyHiking = val;
    }

    public boolean getCurrentlyHiking(){
        return currentlyHiking;
    }

    public ArrayList<GeoPoint> getGeoPointCollection(){
        return pointCollection;
    }

    public double getMinLocationUpdateDistance(){
        return minLocationUpdateDistance;
    }

    public void setMinLocationUpdateDistance(double val){
        minLocationUpdateDistance = val;
    }
}
