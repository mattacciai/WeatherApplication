package com.vt.matt.weatherapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SwipeAdapter extends FragmentStatePagerAdapter {
    public SwipeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //if we go to the first item, this will be the list of locations
        if (position == 0) {
            return new LocationsListFragment();
        }
        //if we are at any other location, 1, this will be the forecast
        return new ForecastFragment();
    }


    @Override
    public int getCount() {
        return 2;
    }
}
