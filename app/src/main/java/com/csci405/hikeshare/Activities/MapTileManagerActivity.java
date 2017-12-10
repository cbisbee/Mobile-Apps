package com.csci405.hikeshare.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csci405.hikeshare.CustomAdapters.CacheAdapter;
import com.csci405.hikeshare.R;
import com.csci405.hikeshare.SqlTileWriterExt;

import org.osmdroid.tileprovider.modules.SqlTileWriter;

import java.util.List;

public class MapTileManagerActivity extends AppCompatActivity implements Runnable {
    Button downloadRegionBtn;
    Button purgeTileCacheBtn;
    Button purgeTileSourceBtn;
    Button browseTileCacheBtn;
    TextView sourceTileCountTV;
    SqlTileWriterExt cache = null;
    CacheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tile_manager);


        downloadRegionBtn = (Button)findViewById(R.id.download_region_Btn);
        purgeTileCacheBtn = (Button)findViewById(R.id.purge_whole_cache_Btn);
        purgeTileSourceBtn = (Button)findViewById(R.id.purge_cache_source_Btn);
        browseTileCacheBtn = (Button)findViewById(R.id.browse_cache_Btn);
        sourceTileCountTV = (TextView)findViewById(R.id.source_tile_count_TV);


        downloadRegionBtn.setOnClickListener((View v) -> {
            // Launch the MyHikesActivity
            Intent intent = new Intent(this, DownloadRegionActivity.class);
            startActivity(intent);
        });

        purgeTileCacheBtn.setOnClickListener((View v) -> {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Purging Tile Cache")
                    .setMessage("Are you sure you want to erase the tile cache?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           purgeCache();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        purgeTileSourceBtn.setOnClickListener((View v) -> {
            purgeTileSource();
        });

        browseTileCacheBtn.setOnClickListener((View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Current Tiles");
            ListView tileBrowseList = new ListView(this);
            tileBrowseList.setAdapter(adapter);
            builder.setView(tileBrowseList);
            final Dialog dialog = builder.create();
            dialog.show();
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        cache = new SqlTileWriterExt();
        adapter = new CacheAdapter(this, cache);
        new Thread(this).start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        cache.onDetach();
        cache = null;
    }

    @Override
    public void run() {
        if (cache==null)
            return;
        List<SqlTileWriterExt.SourceCount> sources = cache.getSources();
        final StringBuilder sb = new StringBuilder("");
        if (sources.isEmpty())
            sb.append("None");
        for (int i = 0; i < sources.size(); i++) {
            sb.append(sources.get(i).source + " : " + sources.get(i).rowCount + "\n");
        }
        long expired = 0;
        if (cache!=null)
            expired = cache.getRowCountExpired();
        sb.append("Expired tiles : " + expired);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextView tv = (TextView) findViewById(R.id.source_tile_count_TV);

                    if (tv != null) {
                        tv.setText(sb.toString());
                    }
                } catch (Exception ex) {

                }
            }
        });
    }

    private void purgeCache() {
        SqlTileWriter sqlTileWriter = new SqlTileWriter();
        boolean b = sqlTileWriter.purgeCache();
        sqlTileWriter.onDetach();
        sqlTileWriter = null;
        if (b)
            Toast.makeText(this, "Cache successfully purged", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Cache purge failed", Toast.LENGTH_LONG).show();
    }

    private void purgeTileSource() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tile Source");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        List<SqlTileWriterExt.SourceCount> sources = cache.getSources();
        for (int i = 0; i < sources.size(); i++) {
            arrayAdapter.add(sources.get(i).source);
        }


        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String item = arrayAdapter.getItem(which);
                new AlertDialog.Builder(MapTileManagerActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Purging Tile Cache")
                        .setMessage("Are you sure you want to erase the tile cache for " + item + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean b = cache.purgeCache(item);
                                if (b)
                                    Toast.makeText(MapTileManagerActivity.this, "Cache successfully purged", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MapTileManagerActivity.this, "Cache purge failed", Toast.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
