/*
 * Created by Shubham Goyal on 5/30/17 11:06 AM
 * Copyright (c) 2017. All rights reserved.
 *
 * File last modified on 7/14/17 10:50 AM
 */

package com.goyalshubham.frame_extractor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shubham on 7/3/2017.
 */

public class GridAdapter extends BaseAdapter {
    private Context context;
    private String[] name;
    private int[] hexVal;

    public GridAdapter(Context c, String s[], int[] i) {
        this.context = c;
        this.name = s;
        this.hexVal = i;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(context);
            grid = inflater.inflate(R.layout.grid_element, null);
            TextView paletteColorName = (TextView) grid.findViewById(R.id.paletteColorName);
            ImageView paletteColor = (ImageView) grid.findViewById(R.id.paletteColor);
            paletteColorName.setText(name[position]);
            String val = hexVal[position] + "";
            if (val.length() == 8) {
                paletteColor.setBackgroundColor(Color.parseColor("#" + hexVal[position]));
            } else if (val.length() == 7) {
                if (val.charAt(0) == '-') {
                    String[] split = val.split("-");
                    String secondString = split[1];
                    String col = "#FF" + secondString;
                    paletteColor.setBackgroundColor(Color.parseColor(col));
                } else {
                    String col = "#F" + hexVal[position];
                    paletteColor.setBackgroundColor(Color.parseColor(col));
                }
            } else {
                paletteColor.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
