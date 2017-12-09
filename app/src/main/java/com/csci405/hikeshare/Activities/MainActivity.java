package com.csci405.hikeshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.csci405.hikeshare.R;

public class MainActivity extends AppCompatActivity {
    Button goHikeBtn;
    Button myHikesBtn;
    Button exploreBtn;
    Button customizeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finding the controls in the view
        goHikeBtn = (Button)findViewById(R.id.main_activity_GoHike_btn);
        myHikesBtn = (Button)findViewById(R.id.main_activity_MyHikes_btn);
        exploreBtn = (Button)findViewById(R.id.main_activity_MapTileManager_btn);
        customizeBtn = (Button)findViewById(R.id.main_activity_Customize_btn);

        //Setting the on click listeners
        goHikeBtn.setOnClickListener((View v) -> {
            // Launch the OsmHike activity
            Intent intent = new Intent(this, OsmHike.class);
            startActivity(intent);

        });

        myHikesBtn.setOnClickListener((View v) -> {
            // Launch the MyHikesActivity
            Intent intent = new Intent(this, MyHikesActivity.class);
            startActivity(intent);
        });

        exploreBtn.setOnClickListener((View v) -> {
            // Launch the MapTileManagerActivity
            Intent intent = new Intent(this,MapTileManagerActivity.class);
            startActivity(intent);
        });

        customizeBtn.setOnClickListener((View v) -> {
            // Launch the customize button
            Intent intent = new Intent(this, CustomizeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume(){

        super.onResume();
    }
}
