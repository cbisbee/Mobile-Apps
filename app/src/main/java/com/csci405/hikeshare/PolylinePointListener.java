package com.csci405.hikeshare;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Charles on 12/12/2017.
 */

public interface PolylinePointListener {
    public void onPolylinePointReceive(ArrayList<GeoPoint> pointlist);
}
