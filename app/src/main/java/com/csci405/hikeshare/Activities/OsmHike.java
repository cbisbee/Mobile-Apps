package com.csci405.hikeshare.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.csci405.hikeshare.BuildConfig;
import com.csci405.hikeshare.CoreActivity;
import com.csci405.hikeshare.Fragments.OverlayItemFragment;
import com.csci405.hikeshare.Listeners.HikeLocationListener;
import com.csci405.hikeshare.Listeners.PolylinePointListener;
import com.csci405.hikeshare.Prefs;
import com.csci405.hikeshare.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class OsmHike extends CoreActivity implements OverlayItemFragment.OnListFragmentInteractionListener, PolylinePointListener {
    IMapController mMapController;
    GeoPoint startPoint;
    GeoPoint currentLongPressPoint;
    MapView mMapView;
    TextView attributionView;
    MyLocationNewOverlay mLocationOverlay;
    CompassOverlay mCompassOverlay;
    RotationGestureOverlay mRotationGestureOverlay;
    ScaleBarOverlay mScaleBarOverlay;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    private int markerDrawResource = R.drawable.marker_default;
    ArrayList<OverlayItem> overlayItemList;
    Prefs mPrefs;
    KmlDocument kmlDoc;
    CacheManager mCacheManager;
    File intentKMLFile;
    FloatingActionButton startStopBtn;
    HikeLocationListener hikeLocationListener;
    LocationManager locationManager;

    boolean recordhike = false;
    boolean kmlDocStarted = false;
    String kmlDocName = "default_name";
    KmlDocument kmlCurHikeDoc;
    File localHikeFile;



    GpsMyLocationProvider mLocationProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPrefs = prefs();
        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mLocationProvider = new GpsMyLocationProvider(ctx);
        hikeLocationListener = new HikeLocationListener();
        hikeLocationListener.setMinLocationUpdateDistance(0.01); //Minimum update distance is .01 miles
        hikeLocationListener.registerForPointsUpdates(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Set<String> sources = mLocationProvider.getLocationSources();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        recordhike = false;

        setContentView(R.layout.activity_osm_hike);
        attributionView = (TextView)findViewById(R.id.osm_hike_attributionTV);
        mMapView = (MapView) findViewById(R.id.map);
        startStopBtn = (FloatingActionButton)findViewById(R.id.start_stopBtn);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordhike){
                    startStopBtn.setImageResource(R.drawable.ic_play_arrow_black_24px);
                    recordhike = false;
                } else {
                    if(mPrefs.follow()) {
                        recordhike = true;
                        startStopBtn.setImageResource(R.drawable.ic_pause_black_24px);
                        if(!kmlDocStarted){
                            //Bring up a dialog here that will prompt user to enter the name of hike
                            askForHikeNameDialog();
                        }
                    } else {
                        Toast.makeText(getBaseContext(),"Following is currently disabled, can't start hiking!",Toast.LENGTH_LONG).show();
                    }
                }
                hikeLocationListener.setCurrentlyHiking(recordhike);
            }
        });
        mCacheManager = new CacheManager(mMapView);
        mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx), mMapView);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),mMapView);
        prepareTheMap(ctx);
    }

    @Override
    public void onResume(){
        super.onResume();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osm_hike, menu);

        if(mPrefs.mapsource().equals("USGS National Map (Topo)")) {
            menu.getItem(0).getSubMenu().getItem(1).setChecked(true);
        }
        else if(mPrefs.mapsource().equals("USGS National Map (Sat)")) {
            menu.getItem(0).getSubMenu().getItem(2).setChecked(true);
        }
        else if(mPrefs.mapsource().equals("Mapnik")) {
            menu.getItem(0).getSubMenu().getItem(0).setChecked(true);
        }
        else if(mPrefs.mapsource().equals("OpenTopoMap")) {
            menu.getItem(0).getSubMenu().getItem(3).setChecked(true);
        }
        else if(mPrefs.mapsource().equals("HikeBikeMap")) {
            menu.getItem(0).getSubMenu().getItem(4).setChecked(true);
        }
        else{
            menu.getItem(0).getSubMenu().getItem(1).setChecked(true);
        }

        if(prefs().offline()){
            menu.getItem(1).setChecked(true);
        } else {
            menu.getItem(1).setChecked(false);
        }

        if(mPrefs.compass()){
            menu.getItem(2).setChecked(true);
        } else {
            menu.getItem(2).setChecked(false);
        }

        if(mPrefs.follow()){
            menu.getItem(3).setChecked(true);
        } else {
            menu.getItem(3).setChecked(false);
        }

        if(mPrefs.powersave()){
            menu.getItem(4).setChecked(true);
        } else {
            menu.getItem(4).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.hikeMenuMapnik:
                mMapView.setTileSource(TileSourceFactory.MAPNIK);
                item.setChecked(true);
                attributionView.setText(getString(R.string.osm_credit));
                return true;
            case R.id.hikeMenuUSGSTopo:
                mMapView.setTileSource(TileSourceFactory.USGS_TOPO);
                item.setChecked(true);
                attributionView.setText(getString(R.string.usgs_credit));
                return true;
            case R.id.hikeMenuUSGSSat:
                mMapView.setTileSource(TileSourceFactory.USGS_SAT);
                item.setChecked(true);
                attributionView.setText(getString(R.string.usgs_credit));
                return true;
            case R.id.hikeMenuOpenTopo:
                mMapView.setTileSource(TileSourceFactory.OpenTopo);
                item.setChecked(true);
                attributionView.setText(getString(R.string.open_topo_map_credit));
                return true;
            case R.id.hikeMenuHikeBike:
                mMapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
                item.setChecked(true);
                attributionView.setText(getString(R.string.osm_credit));
                return true;
            case R.id.hikeMenuOffline:
                if(item.isChecked()){
                    //Go into online mode
                    mMapView.setUseDataConnection(true);
                    mMapView.invalidate();
                    item.setChecked(false);
                } else {
                    //Go into offline mode
                    mMapView.setUseDataConnection(false);
                    mMapView.invalidate();
                    item.setChecked(true);
                }
                return true;
            case R.id.hikeMenuCompass:
                if(item.isChecked()){
                    //remove the compass
                    mCompassOverlay.disableCompass();
                    mMapView.invalidate();
                    item.setChecked(false);
                } else {
                    //add the compass
                    mCompassOverlay.enableCompass(new InternalCompassOrientationProvider(this));
                    mMapView.invalidate();
                    item.setChecked(true);
                }
                return true;
            case R.id.hikeMenuFollow:
                if(item.isChecked()){
                    //Disable tracking
                    item.setChecked(false);
                    mLocationOverlay.disableFollowLocation();
                    mLocationOverlay.disableMyLocation();
                    hikeLocationListener.setCurrentlyHiking(false);
                    mMapView.invalidate();
                }else {
                    //Enable tracking
                    item.setChecked(true);
                    mLocationOverlay.enableFollowLocation();
                    mLocationOverlay.enableMyLocation();
                    mMapView.invalidate();
                }
                return true;
            case R.id.hikeMenuPowerSave:
                if(item.isChecked()){
                    //Go out of powersave mode
                    item.setChecked(false);

                } else {
                    //go into powersave mode
                    item.setChecked(true);
                }
                return true;
            case R.id.hikeMenuOpenKML:
                //Add a method to actually get the file name
                Bundle fileBundle = getIntent().getBundleExtra("KMLFILE");
                if(fileBundle != null){
                    //Don't do anything
                    Object intentFile = fileBundle.get("SerialFile");
                    intentKMLFile = (File)intentFile;
                    addKmlGivenFile(intentKMLFile);
                } else {
                    Toast.makeText(getBaseContext(),"No hike selected, go to MyHikes and select a hike!",Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.trace:
                //geoPointsIntoPolyline(hikeLocationListener.getGeoPointCollection());
                ArrayList<GeoPoint> test = new ArrayList<>();
                test.add(new GeoPoint(39.0,-108.0));
                test.add(new GeoPoint(39.1,-108.0));
                test.add(new GeoPoint(39.2,-108.0));
                test.add(new GeoPoint(39.3,-108.0));
                test.add(new GeoPoint(39.4,-108.0));
                test.add(new GeoPoint(39.5,-108.1));
                test.add(new GeoPoint(39.3,-108.5));
                test.add(new GeoPoint(39.1,-108.3));
                test.add(new GeoPoint(39.7,-108.4));
                test.add(new GeoPoint(39.8,-108.5));
                test.add(new GeoPoint(39.7,-108.1));
                test.add(new GeoPoint(39.9,-108.2));
                geoPointsIntoPolyline(test);

                //This is the actual call that needs to be made
                //geoPointsIntoPolyline(hikeLocationListener.getGeoPointCollectionAndClear());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPolylinePointReceive(ArrayList<GeoPoint> pointlist){
        geoPointsIntoPolyline(pointlist);
    }

    private void askForHikeNameDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Enter the name of this hike");
        final EditText input = new EditText(this);
        b.setView(input);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
               kmlDocName = input.getText().toString();
                kmlCurHikeDoc = new KmlDocument();
                localHikeFile = kmlCurHikeDoc.getDefaultPathForAndroid(kmlDocName + ".kml");
                kmlCurHikeDoc.mKmlRoot.mName = kmlDocName;
                kmlCurHikeDoc.saveAsKML(localHikeFile);
                kmlDocStarted = true;
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.show();
    }

    public void addKmlGivenFileName(String _fname){
        kmlDoc = new KmlDocument();
        File localFile = kmlDoc.getDefaultPathForAndroid(_fname);
        kmlDoc.parseKMLFile(localFile);
        FolderOverlay kmlOverlay = (FolderOverlay)kmlDoc.mKmlRoot.buildOverlay(mMapView, null, null, kmlDoc);
        mMapView.getOverlays().add(kmlOverlay);
        BoundingBox bb = kmlDoc.mKmlRoot.getBoundingBox();
        mMapView.getController().setCenter(bb.getCenter());
        mMapView.zoomToBoundingBox(bb,false); //This pretty much has to be false for this to actually work, yay!
        mMapView.invalidate();
    }
    public void addKmlGivenFile(File _kmlFile){
        kmlDoc = new KmlDocument();
        kmlDoc.parseKMLFile(_kmlFile);
        FolderOverlay kmlOverlay = (FolderOverlay)kmlDoc.mKmlRoot.buildOverlay(mMapView, null, null, kmlDoc);
        mMapView.getOverlays().add(kmlOverlay);
        BoundingBox bb = kmlDoc.mKmlRoot.getBoundingBox();
        mMapView.getController().setCenter(bb.getCenter());
        mMapView.zoomToBoundingBox(bb,false); //This pret
        mMapView.invalidate();
    }

    public void geoPointsIntoPolyline(ArrayList<GeoPoint> pointList){
        Polyline polyline;
        List<List<GeoPoint>> polyLines = new ArrayList<>();
        polyLines.add(pointList);
        for (int i = 0; i < polyLines.size(); i++) {
            polyline = new Polyline();
            int count = polyline.getNumberOfPoints();
            polyline.setPoints(polyLines.get(i));
            count = polyline.getNumberOfPoints();
            polyline.setColor(Color.RED);
            polyline.setWidth(5);
            polyline.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mMapView));
            polyline.setTitle("This is your hike path!");
            mMapView.getOverlays().add(polyline);
            kmlCurHikeDoc.mKmlRoot.addOverlay(polyline,kmlCurHikeDoc);
        }
        mMapView.invalidate();
        kmlCurHikeDoc.saveAsKML(localHikeFile);
    }


    public void selectLongPressAction() {
        DialogFragment newFragment = new OverlayItemFragment();
        newFragment.show(getSupportFragmentManager(), "action");
    }

    public void addCustomMarker(int resourceId, GeoPoint markerLocation, Context ctx){
        OverlayItem olItem = new OverlayItem("Here", "SampleDescription", markerLocation);
        Drawable newMarker = ctx.getResources().getDrawable(markerDrawResource);
        olItem.setMarker(newMarker);
    }

    public void addCustomMarker(int resourceId, String title, String description, Context ctx){
        OverlayItem olItem = new OverlayItem(title, description, currentLongPressPoint);
        Drawable newMarker = ContextCompat.getDrawable(ctx, resourceId);
        olItem.setMarker(newMarker);
        mOverlay.addItem(olItem);
        if(kmlDocStarted){
            Marker marker = new Marker(mMapView);
            marker.setImage(newMarker);
            marker.setPosition(currentLongPressPoint);
            kmlCurHikeDoc.mKmlRoot.addOverlay(marker,kmlCurHikeDoc);
            kmlCurHikeDoc.saveAsKML(localHikeFile);
        }
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int resource) {
        // User touched the dialog's positive button
        //TODO figure out from a user interface perspective how we want to manage titles and descriptions of markers if we do at all
        //TODO figure out if the idea of 'cancel' and 'ok' are really what we want and fix this ugly logic
        if(resource != -1) {
            addCustomMarker(resource, "New Marker", "Description", this);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int resource) {
        // User touched the dialog's negative button
        //TODO: Does there need to be any logic in here?
    }

    public void prepareTheMap(Context _ctx){
        if(mPrefs.mapsource().equals("USGS National Map (Topo)")) {
            mMapView.setTileSource(TileSourceFactory.USGS_TOPO);
            attributionView.setText(getString(R.string.usgs_credit));
        }
        else if(mPrefs.mapsource().equals("USGS National Map (Sat)")) {
            mMapView.setTileSource(TileSourceFactory.USGS_SAT);
            attributionView.setText(getString(R.string.usgs_credit));
        }
        else if(mPrefs.mapsource().equals("Mapnik")) {
            mMapView.setTileSource(TileSourceFactory.MAPNIK);
            attributionView.setText(getString(R.string.usgs_credit));
        }
        else if(mPrefs.mapsource().equals("OpenTopoMap")) {
            mMapView.setTileSource(TileSourceFactory.OpenTopo);
            attributionView.setText(getString(R.string.open_topo_map_credit));
        }
        else if(mPrefs.mapsource().equals("HikeBikeMap")) {
            mMapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
            attributionView.setText(getString(R.string.hike_bike_map));
        }
        else{
            mMapView.setTileSource(TileSourceFactory.USGS_TOPO);
            attributionView.setText(getString(R.string.usgs_credit));
        }

        if(mPrefs.zoom()){
            //Add the Zoom buttons to the map and allow the user to zoom with their fingers
            mMapView.setBuiltInZoomControls(true);
        }

        if(mPrefs.compass()){
            mCompassOverlay.enableCompass();
        } else {
            mCompassOverlay.disableCompass();
        }
        mMapView.getOverlays().add(this.mCompassOverlay);

        if(mPrefs.scalebar()){
            mScaleBarOverlay = new ScaleBarOverlay(mMapView);
            mScaleBarOverlay.setCentred(true);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScaleBarOverlay.setScaleBarOffset(metrics.widthPixels / 2, 10);
            mMapView.getOverlays().add(this.mScaleBarOverlay);
        }

        if(prefs().offline()){
            mMapView.setUseDataConnection(false);
        } else {
            mMapView.setUseDataConnection(true);
        }

        if(prefs().follow()){
            //This is the follow location stuff
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
            mMapView.getOverlays().add(this.mLocationOverlay);

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, hikeLocationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                }
            } catch (SecurityException e){
                Toast.makeText(getBaseContext(),"Can't get location, permission was not granted for this app!",Toast.LENGTH_LONG).show();
            }
        }


        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(5);

        //This enables the gestures for rotations and zooming
        mRotationGestureOverlay = new RotationGestureOverlay(_ctx, mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);

        overlayItemList = new ArrayList<OverlayItem>();
        //items.add(new OverlayItem("Title", "Description", new GeoPoint(39.143407,-108.701759))); // Lat/Lon decimal degrees
        //the overlay
        mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(overlayItemList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        //TODO: figure out the functionality for this
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        //TODO: make item long press bring up a dialog to ask the user if they want to delete the marker
                        overlayItemList.clear();
                        mMapView.invalidate();
                        return false;
                    }
                },_ctx);
        mOverlay.setFocusItemsOnTap(true);
        mMapView.getOverlays().add(mOverlay);



        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                //Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();
                //TODO: Figure out this logic
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                currentLongPressPoint = point;
                selectLongPressAction();
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        mMapView.getOverlays().add(OverlayEvents);

        startPoint = new GeoPoint(39.143407, -108.701759);
        mMapController.setCenter(startPoint);


        mMapView.invalidate();
    }

    @Override
    public void onDestroy(){
        locationManager.removeUpdates(hikeLocationListener);
        hikeLocationListener.unregisterForPointsUpdates(this);
        super.onDestroy();
    }
}
