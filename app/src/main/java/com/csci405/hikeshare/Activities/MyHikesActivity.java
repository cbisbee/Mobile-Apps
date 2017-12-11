package com.csci405.hikeshare.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.csci405.hikeshare.CustomAdapters.HikeAdaptor;
import com.csci405.hikeshare.R;

import java.io.File;
import java.util.List;

public class MyHikesActivity extends AppCompatActivity {
    HikeAdaptor hikeAdaptor;
    ListView myhikesView;
    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_hikes);

        myhikesView = (ListView)findViewById(R.id.my_hikes_LV);

        File dir = new File(Environment.getExternalStorageDirectory(), "kml");
        ctx = this;
        final File[] filelist = dir.listFiles();
        hikeAdaptor = new HikeAdaptor(this,filelist);
        myhikesView.setAdapter(hikeAdaptor);
        myhikesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File curKMLFile = filelist[position];
                Intent intent = new Intent(ctx, OsmHike.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SerialFile", curKMLFile);
                intent.putExtra("KMLFILE",bundle);
                startActivity(intent);
            }
        });
    }
}
