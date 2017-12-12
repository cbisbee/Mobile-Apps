package com.csci405.hikeshare.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.csci405.hikeshare.Hike;
import com.csci405.hikeshare.R;

import org.osmdroid.bonuspack.kml.KmlDocument;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Charles on 12/10/2017.
 */

public class HikeAdaptor extends ArrayAdapter {
    File[] filelist;
    public HikeAdaptor(Context context, File[] _filelist) {
        super(context, R.layout.item_cache);
        filelist = _filelist;
    }

    @Override
    public int getCount() {
        return filelist.length;
    }

    @Override
    public Object getItem(int id) {
        File curFile = filelist[id];
        Hike item = new Hike("","");
        KmlDocument kmlDoc = new KmlDocument();
        kmlDoc.parseKMLFile(curFile);
        item.hikeName = kmlDoc.mKmlRoot.mItems.get(0).mName;
        item.lastModified = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
                new Date(curFile.lastModified())
        );
        return item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_hike_list_item, parent, false);
        }
        Hike p = (Hike) getItem(position);
        if (p != null) {
            // Find fields to populate in inflated template
            TextView name = (TextView)convertView.findViewById(R.id.tvhikeName);
            TextView lastMod = (TextView)convertView.findViewById(R.id.tvlastmodifiedhike);

            name.setText(p.hikeName);
            lastMod.setText(p.lastModified);
        }
        return convertView;
    }
}
