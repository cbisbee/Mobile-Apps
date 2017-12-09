package com.csci405.hikeshare.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.csci405.hikeshare.AsyncKmlRetriever;
import com.csci405.hikeshare.BuildConfig;
import com.csci405.hikeshare.CoreActivity;
import com.csci405.hikeshare.Fragments.OverlayItemFragment;
import com.csci405.hikeshare.Prefs;
import com.csci405.hikeshare.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;


public class OsmHike extends CoreActivity implements OverlayItemFragment.OnListFragmentInteractionListener {
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

    GpsMyLocationProvider mLocationProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPrefs = prefs();
        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mLocationProvider = new GpsMyLocationProvider(ctx);
        Set<String> sources = mLocationProvider.getLocationSources();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_osm_hike);

        attributionView = (TextView)findViewById(R.id.osm_hike_attributionTV);

        mMapView = (MapView) findViewById(R.id.map);


        prepareTheMap(ctx);






        //your items
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
                },ctx);
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




        /*
        //KML Stuff
        kmlDoc = new KmlDocument();
        //File localFile = kmlDoc.getDefaultPathForAndroid("my_route.kml");
        //kmlDoc.parseKMLFile(localFile);
        String url = "http://mapsengine.google.com/map/kml?forcekml=1&mid=12phHnQP7CcPH07FaT9p1YyaNvCE";
        //String url = "http://www.yournavigation.org/api/1.0/gosmore.php?format=kml&flat=52.215676&flon=5.963946&tlat=52.2573&tlon=6.1799%27%3Ehttp://www.yournavigation.org/api/1.0/gosmore.php?format=kml&amp;flat=52.215676&amp;flon=5.963946&amp;tlat=52.2573&amp;tlon=6.1799";
        //String url = "testfail";

        AsyncKmlRetriever.AsyncGet kmlUrlRetriever = new AsyncKmlRetriever.AsyncGet() {
            @Override
            public void getKml() {
                try {
                    kmlDoc.parseKMLUrl(url);

                    //Set up the map overlay for the KML
                    FolderOverlay kmlOverlay = (FolderOverlay)kmlDoc.mKmlRoot.buildOverlay(mMapView, null, null, kmlDoc);
                    mMapView.getOverlays().add(kmlOverlay);
                    BoundingBox bb = kmlDoc.mKmlRoot.getBoundingBox();
                    mMapView.getController().setCenter(bb.getCenter());
                    mMapView.zoomToBoundingBox(bb,false); //This pretty much has to be false for this to actually work, yay!
                    mMapView.invalidate();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        AsyncKmlRetriever asyncGetter = new AsyncKmlRetriever(getApplicationContext(),kmlUrlRetriever);
        asyncGetter.execute();

        //kmlDoc.saveAsKML(localFile);
        */
    }

    @Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
            mCompassOverlay = new CompassOverlay(_ctx, new InternalCompassOrientationProvider(_ctx), mMapView);
            mCompassOverlay.enableCompass();
            mMapView.getOverlays().add(this.mCompassOverlay);
        }

        if(mPrefs.scalebar()){
            mScaleBarOverlay = new ScaleBarOverlay(mMapView);
            mScaleBarOverlay.setCentred(true);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mScaleBarOverlay.setScaleBarOffset(metrics.widthPixels / 2, 10);
            mMapView.getOverlays().add(this.mScaleBarOverlay);
        }

        //This is the default start lcoation
        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(5);
        startPoint = new GeoPoint(39.143407, -108.701759);
        mMapController.setCenter(startPoint);

        //This is the follow location stuff
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(_ctx),mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        //This enables the gestures for rotations and zooming
        mRotationGestureOverlay = new RotationGestureOverlay(_ctx, mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);
    }
}
