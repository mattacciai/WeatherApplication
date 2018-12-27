package com.vt.matt.weatherapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LocationsListFragment extends Fragment {

    public static ListView listView;
    private static EditText addLocationEditText;
    private static ImageView deleteImageView;
    public static ArrayList<Location> locationsList;
    public static final String TAG = "LOCATIONS_LIST_FRAGMENT";
    public static LocationListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view;

        //makes sure fragments are not loaded on top of each other
        if (container != null) {
            container.removeAllViews();
        }

        //inflates view and sets font
        view = inflater.inflate(R.layout.fragment_locations_layout, container, false);
        android.graphics.Typeface custom_font = android.graphics.Typeface.createFromAsset(getActivity().getAssets(), "fonts/Hero Light.otf");

        //initializes all views
        listView = view.findViewById(R.id.locationsListView);
        addLocationEditText = view.findViewById(R.id.addCityEditText);
        deleteImageView = view.findViewById(R.id.deleteCityImageView);
        addLocationEditText.setTypeface(custom_font);
        locationsList = new ArrayList<>();

        //builds the locations array
        //the locations array is made up of the contents of cities.txt
        //this is the list displayed on the select location screen
        Scanner scan = new Scanner(FetchData.load().toString());
        while (scan.hasNextLine()){
            try {
                String location = scan.nextLine();
                locationsList.add(new Location(location, 70, false));
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "No elements to display", Toast.LENGTH_SHORT).show();
                locationsList.add(new Location("new york", 70, false));
            }
        }

        //sets the custom list adapter for the locations list view
        adapter = new LocationListAdapter(MainActivity.context, R.layout.adapter_list_layout, locationsList);
        listView.setAdapter(adapter);

        //gets the temperature data for each element in locationsList
        //data.execute() will set the temperatures into each view
        for (Location loc : LocationsListFragment.locationsList) {
            FetchData data = new FetchData(loc.getName());
            data.execute();
        }

        //move the user into the forecast view for the location clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String location = locationsList.get(i).getName();

                //display the location clicked on
                FetchData newLocation = new FetchData(location);
                newLocation.execute();

                //change to the location that was clicked on
                MainActivity.viewPager.setCurrentItem(1);

            }
        });

        //clears the add location text when it is clicked
        addLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationEditText.getText().clear();
            }
        });

        ///when the enter key is pressed, this adds the location to the locations file
        //FetchData manages what locations are valid and when to add them to the file
        addLocationEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int key, KeyEvent event) {
                Location newLocation = new Location(addLocationEditText.getText().toString(), 70, false);
                if (event.getAction() == event.ACTION_DOWN) {
                    if (key == event.KEYCODE_ENTER) {
                        FetchData data = new FetchData(newLocation.getName());
                        data.execute();
                        addLocationEditText.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });

        //when the delete button is pressed, transfer users to the delete fragment
        //this allows them to select when locations they would like to delete
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DeleteViewFragment fragment = new DeleteViewFragment();
                fm.beginTransaction().
                        replace(R.id.fragmentContainer, fragment).
                        commit();
            }
        });

        return view;
    }

    /**
     * Determine if a locations with the name "name" is already in the ArrayList loc
     * @param name The location to determine if it is in the array
     * @param loc The array to check the name against
     * @return True if the name is in the array, false if not
     */
    public static boolean sameNames(String name, ArrayList<Location> loc) {
        for (Location location : loc) {
            if (name.toLowerCase().equals(location.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
