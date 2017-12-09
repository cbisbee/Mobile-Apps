package com.csci405.hikeshare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Charles on 12/1/2017.
 */

public class AsyncKmlRetriever extends AsyncTask<Void,Void,Void> {
    Context mContext;
    String TAG = "AsyncKmlRetriever";

    public interface AsyncGet{
        public void getKml();
    }

    private final AsyncGet kmlGetter;

    public AsyncKmlRetriever(Context context, AsyncGet kmlGetter){
        mContext = context;
        this.kmlGetter = kmlGetter;
    }



    @Override
    protected void onPreExecute() {
        /*
        Context context = mContext;
        CharSequence text = "Retrieving map data...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        */
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Runs on the background thread
        try {
            this.kmlGetter.getKml();
        } catch(Exception e) {
            Log.e(TAG,e + " Couldn't retrieve the map data!");

            Context context = mContext;
            CharSequence text = "Couldn't retrieve the map data!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void res) {
        /*
        Context context = mContext;
        CharSequence text = "Succesfully retrieved the map data!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        */
    }

}