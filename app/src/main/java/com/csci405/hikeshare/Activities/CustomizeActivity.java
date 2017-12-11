package com.csci405.hikeshare.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.csci405.hikeshare.CoreActivity;
import com.csci405.hikeshare.Prefs;
import com.csci405.hikeshare.R;

public class CustomizeActivity extends CoreActivity {
    Prefs mPrefs;
    ToggleButton followMeToggle;
    ToggleButton compassToggle;
    ToggleButton scalebarToggle;
    ToggleButton zoomToggle;
    ToggleButton powersaveToggle;
    ToggleButton offlineToggle;
    Spinner mapSourceSpinner;

    @Override
    protected void onResume(){
        if(prefs().follow()){
            followMeToggle.setChecked(true);
        } else {
            followMeToggle.setChecked(false);
        }

        if(prefs().compass()){
            compassToggle.setChecked(true);
        } else {
            compassToggle.setChecked(false);
        }

        if(prefs().scalebar()){
            scalebarToggle.setChecked(true);
        } else {
            scalebarToggle.setChecked(false);
        }

        if(prefs().zoom()){
            zoomToggle.setChecked(true);
        } else {
            zoomToggle.setChecked(false);
        }

        if(prefs().powersave()){
            powersaveToggle.setChecked(true);
        } else {
            powersaveToggle.setChecked(false);
        }

        if(prefs().offline()){
            offlineToggle.setChecked(true);
        } else {
            offlineToggle.setChecked(false);
        }

        if(prefs().mapsource().equals("Mapnik")){
            mapSourceSpinner.setSelection(0);
        }

        if(prefs().mapsource().equals("USGS National Map (Topo)")){
            mapSourceSpinner.setSelection(1);
        }

        if(prefs().mapsource().equals("USGS National Map (Sat)")){
            mapSourceSpinner.setSelection(2);
        }

        if(prefs().mapsource().equals("OpenTopoMap")){
            mapSourceSpinner.setSelection(3);
        }

        if(prefs().mapsource().equals("HikeBikeMap")){
            mapSourceSpinner.setSelection(4);
        }

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        followMeToggle = (ToggleButton)findViewById(R.id.show_map_markers_Toggle);
        compassToggle = (ToggleButton)findViewById(R.id.show_map_compass_Toggle);
        scalebarToggle = (ToggleButton)findViewById(R.id.show_scale_bar_Toggle);
        zoomToggle = (ToggleButton)findViewById(R.id.show_zoom_buttons_Toggle);
        powersaveToggle = (ToggleButton)findViewById(R.id.powersave_mode_Toggle);
        offlineToggle = (ToggleButton)findViewById(R.id.soffline_mode_enabled_Toggle);
        mapSourceSpinner = (Spinner)findViewById(R.id.map_source_Spinner);

        followMeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().follow(true);
                } else {
                    prefs().follow(false);
                }
            }
        });

        compassToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().compass(true);
                } else {
                    prefs().compass(false);
                }
            }
        });

        scalebarToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().scalebar(true);
                } else {
                    prefs().scalebar(false);
                }
            }
        });

        zoomToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().zoom(true);
                } else {
                    prefs().zoom(false);
                }
            }
        });

        powersaveToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().powersave(true);
                } else {
                    prefs().powersave(false);
                }
            }
        });

        offlineToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs().offline(true);
                } else {
                    prefs().offline(false);
                }
            }
        });

        mapSourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        prefs().mapsource("Mapnik");
                        break;
                    case 1:
                        prefs().mapsource("USGS National Map (Topo)");
                        break;
                    case 2:
                        prefs().mapsource("USGS National Map (Sat)");
                        break;
                    case 3:
                        prefs().mapsource("OpenTopoMap");
                        break;
                    case 4:
                        prefs().mapsource("HikeBikeMap");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }
}
