package com.csci405.hikeshare.Activities;

import android.content.Context;
import android.location.LocationProvider;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.csci405.hikeshare.BuildConfig;
import com.csci405.hikeshare.CoreActivity;
import com.csci405.hikeshare.Prefs;
import com.csci405.hikeshare.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Map;
import java.util.Set;

public class DownloadRegionActivity extends CoreActivity {
    Prefs mPrefs;
    CacheManager mCacheManager;
    CacheManager.CacheManagerTask downloadingTask=null;
    MapView mMapView;
    GpsMyLocationProvider mLocationProvider;
    IMapController mMapController;
    RotationGestureOverlay mRotationGestureOverlay;
    Button downloadButton;

    boolean downloadByPathMethod = false;

    TextView attributionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_region);
        final Context ctx = getApplicationContext();
        mPrefs = prefs();


        mMapView = (MapView)findViewById(R.id.map);
        attributionView = (TextView)findViewById(R.id.osm_hike_attributionTV);
        downloadButton = (Button)findViewById(R.id.downloadBtn);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mLocationProvider = new GpsMyLocationProvider(ctx);
        Set<String> sources = mLocationProvider.getLocationSources();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mCacheManager = new CacheManager(mMapView);

        prepareTheMap(ctx);



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


        mMapView.setUseDataConnection(true);


        //This is the default start lcoation
        mMapView.setMultiTouchControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(5);
        GeoPoint startPoint = new GeoPoint(39.143407, -108.701759);
        mMapController.setCenter(startPoint);


        //This enables the gestures for rotations and zooming
        mRotationGestureOverlay = new RotationGestureOverlay(_ctx, mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download, menu);
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
            case R.id.downloadMenuRegion:
                item.setChecked(true);
                downloadButton.setText(R.string.download_region_title);
                downloadByPathMethod = false;
                return true;
            case R.id.downloadMenuPath:
                item.setChecked(true);
                downloadButton.setText(R.string.download_path_title);
                downloadByPathMethod = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
