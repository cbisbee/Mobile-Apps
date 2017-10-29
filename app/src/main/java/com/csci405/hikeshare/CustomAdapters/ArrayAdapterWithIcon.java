package com.csci405.hikeshare.CustomAdapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.csci405.hikeshare.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Charles on 10/28/2017.
 */

public class ArrayAdapterWithIcon extends ArrayAdapter<String> {
    private List<Integer> images;

    public ArrayAdapterWithIcon(Context _context,List<String> _items, List<Integer> _images){
        super(_context, R.layout.fragment_overlayitem, R.id.content,_items);
        this.images = _images;
    }

    public ArrayAdapterWithIcon(Context _context, List<String> _items, Integer[] _images){
        super(_context,R.layout.fragment_overlayitem, R.id.content,_items);
        this.images = Arrays.asList(_images);
    }

    public ArrayAdapterWithIcon(Context _context, int _items, int _images){
        //This may need to actually get getTextArray
        super(_context, R.layout.fragment_overlayitem, R.id.content,_context.getResources().getStringArray(_items));

        final TypedArray imgs = _context.getResources().obtainTypedArray(_images);
        this.images = new ArrayList<Integer>(){
            {
                for(int i = 0; i < imgs.length(); ++i){
                    add(imgs.getResourceId(i,-1));
                }
            }
        };

        imgs.recycle();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position,convertView,parent);
        TextView textView = (TextView)view.findViewById(R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(images.get(position), 0, 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(images.get(position), 0, 0, 0);
        }
        textView.setCompoundDrawablePadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
        return view;
    }
}
