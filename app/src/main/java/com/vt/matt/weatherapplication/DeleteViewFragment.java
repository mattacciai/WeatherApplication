package com.vt.matt.weatherapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DeleteViewFragment extends android.support.v4.app.Fragment {

    private ListView deleteLocationsListView;
    private DeleteListAdapter adapter;
    private ImageView confirmDeleteImageView;
    private ImageView backImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view;

        //makes sure fragments are not loaded on top of each other
        if (container != null) {
            container.removeAllViews();
        }

        //inflates view and finds all of the views
        view = inflater.inflate(R.layout.fragment_delete_layout, container, false);
        deleteLocationsListView = view.findViewById(R.id.deleteLocationsListView);
        confirmDeleteImageView = view.findViewById(R.id.deleteConfirmImageView);
        backImageView = view.findViewById(R.id.backArrowImageView);

        //sets the adapter for the list
        adapter = new DeleteListAdapter(MainActivity.context, R.layout.adapter_delete_layout, LocationsListFragment.locationsList);
        deleteLocationsListView.setAdapter(adapter);

        //if a location is already selected, deselect it, if not, select the item
        //notify the adapter of this change to update the image
        deleteLocationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Location loc = (Location) deleteLocationsListView.getItemAtPosition(position);
                if (loc.isSelected()) {
                    loc.setSelected(false);
                } else {
                    loc.setSelected(true);
                }
                adapter.notifyDataSetChanged();
            }
        });

        //when the delete button is pressed again
        //delete the locations from the file
        confirmDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromFile();
                adapter.notifyDataSetChanged();
            }
        });

        //switches the user back into the locations list fragment
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                LocationsListFragment fragment = new LocationsListFragment();
                ft.replace(R.id.deleteViewRelativeLayout, fragment).commit();
            }
        });
        return view;
    }

    /**
     * Deletes all selected locations from the file
     */
    private void deleteFromFile() {
        ArrayList<Location> locs = LocationsListFragment.locationsList;
        for (int i = locs.size() - 1; i >= 0; i--) {
            if (locs.get(i).isSelected()) {
                locs.remove(i);
            }
        }
       updateFile(locs);
    }

    /**
     * Updates the file to contain all locations in locs array
     * @param locs The array to update the file to
     */
    private void updateFile(ArrayList<Location> locs) {
        StringBuilder sb = new StringBuilder();
        FileOutputStream fos = null;
        try {
            fos = MainActivity.context.openFileOutput("cities.txt", MainActivity.context.MODE_PRIVATE);
            for (Location loc : locs) {
                sb.append(loc.getName() + "\n");
            }
            fos.write(sb.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
