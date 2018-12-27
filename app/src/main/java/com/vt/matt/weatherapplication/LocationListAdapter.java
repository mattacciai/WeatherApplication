package com.vt.matt.weatherapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationListAdapter extends ArrayAdapter<Location> {

    private Context context;
    private int resource;
    private String place;
    private int temp;
    public static TextView nameTextView;
    public static TextView tempTextView;

    public LocationListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Location> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        android.graphics.Typeface custom_font = android.graphics.Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/Hero Light.otf");
        place = getItem(position).getName();
        temp = getItem(position).getTemp();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        nameTextView = convertView.findViewById(R.id.listLocationTextView);
        tempTextView = convertView.findViewById(R.id.listTemperatureTextView);
        nameTextView.setTypeface(custom_font);
        tempTextView.setTypeface(custom_font);

        nameTextView.setText(place);
        tempTextView.setText("" + temp + "°");

        return convertView;
    }
}
