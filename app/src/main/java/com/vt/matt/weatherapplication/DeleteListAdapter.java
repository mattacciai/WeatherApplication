package com.vt.matt.weatherapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeleteListAdapter extends ArrayAdapter<Location> {

    private Context context;
    private int resource;
    private ImageView checkImageView;

    public DeleteListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Location> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        android.graphics.Typeface custom_font = android.graphics.Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/Hero Light.otf");
        String place = getItem(position).getName();
        int temp = getItem(position).getTemp();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView nameTextView = convertView.findViewById(R.id.delLocationTextView);
        TextView tempTextView = convertView.findViewById(R.id.delTemperatureTextView);
        checkImageView = convertView.findViewById(R.id.selectButtonImageView);

        nameTextView.setTypeface(custom_font);
        tempTextView.setTypeface(custom_font);

        nameTextView.setText(place);
        tempTextView.setText("" + temp + "°");

        //sets the image of the button to reflect if it is selected or not
        if (getItem(position).isSelected()) {
            checkImageView.setImageResource(R.drawable.ic_select_button_clicked);
        } else {
            checkImageView.setImageResource(R.drawable.ic_select_button);
        }

        return convertView;
    }
}
