package com.csci405.hikeshare.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.csci405.hikeshare.CustomAdapters.ArrayAdapterWithIcon;
import com.csci405.hikeshare.R;

import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OverlayItemFragment extends DialogFragment {

    private OnListFragmentInteractionListener mListener;
    private int resourceId = -1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OverlayItemFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] overlayItemsArray = getResources().getStringArray(R.array.overlay_items);
        List<String> overlayItemsArrayList = Arrays.asList(overlayItemsArray);
        final Integer[] icons = new Integer[]
                {R.drawable.ic_archery,R.drawable.ic_boat_ramp,R.drawable.ic_boating,R.drawable.ic_campground,R.drawable.ic_canoe,R.drawable.ic_climbing,
                R.drawable.ic_natural_feature,R.drawable.ic_cross_country_skiing,R.drawable.ic_fishing_pier,R.drawable.ic_hang_gliding,R.drawable.ic_kayaking,
                R.drawable.ic_map_pin,R.drawable.ic_shield,R.drawable.ic_route,R.drawable.ic_snowboarding,R.drawable.ic_swimming};

        ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), overlayItemsArrayList, icons);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resourceId = icons[i];
                mListener.onDialogPositiveClick(OverlayItemFragment.this, resourceId);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resourceId = -1;
                mListener.onDialogNegativeClick(OverlayItemFragment.this,resourceId);
            }
        });

        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onDialogPositiveClick(DialogFragment dialog,int resource);
        void onDialogNegativeClick(DialogFragment dialog, int resource);
    }
}
