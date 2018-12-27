package com.vt.matt.weatherapplication;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ForecastFragment extends Fragment{
    public static TextView temperature;
    public static TextView location;
    public static TextView maxAndMin;
    public static TextView weather;
    public static ImageView viewWeather;
    public static TextView[][] forecastInfo;
    public static ImageView[] weatherViews;
    private static ScrollView scrollView;
    private static ImageView downArrow;

    public static final String DEFAULT_LOCATION = "Blacksburg";

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view;

        //inflates view
        view = inflater.inflate(R.layout.fragment_forecast_layout, container, false);
        getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //sets up views and fonts
        this.initializeViews(view);
        this.initializeFonts();

        //sets up the arrow that shows the user which direction to scroll
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                float totalScrollHeight = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                float scrollPercent = scrollView.getScrollY() / totalScrollHeight;
                downArrow.setRotation(-180 * scrollPercent);
            }
        });

        return view;
    }

    /**
     * Initializes all the views into the forecastInfo and weatherViews arrays
     * @param view The view containing all the text and image views
     */
    private void initializeViews(View view) {
        //initialize arrays to hold views
        forecastInfo = new TextView[3][3];
        weatherViews = new ImageView[3];

        //initialize all main views for main page
        temperature = view.findViewById(R.id.temperatureTextView);
        location = view.findViewById(R.id.locationTextView);
        maxAndMin = view.findViewById(R.id.maxAndMinTextView);
        weather = view.findViewById(R.id.weatherTextView);
        viewWeather = view.findViewById(R.id.viewWeatherImageView);
        scrollView = view.findViewById(R.id.scrollView);
        downArrow = view.findViewById(R.id.downArrowImageView);


        //initialize views for the three day forecast
        forecastInfo[0][0] = view.findViewById(R.id.dayOneNameTextView);
        forecastInfo[0][1] = view.findViewById(R.id.dayOneTemperatureTextView);
        forecastInfo[0][2] = view.findViewById(R.id.dayOneWeatherTextView);
        weatherViews[0] = view.findViewById(R.id.viewWeatherDayOneImageView);

        forecastInfo[1][0] = view.findViewById(R.id.dayTwoNameTextView);
        forecastInfo[1][1] = view.findViewById(R.id.dayTwoTemperatureTextView);
        forecastInfo[1][2] = view.findViewById(R.id.dayTwoWeatherTextView);
        weatherViews[1] = view.findViewById(R.id.viewWeatherDayTwoImageView);

        forecastInfo[2][0] = view.findViewById(R.id.dayThreeNameTextView);
        forecastInfo[2][1] = view.findViewById(R.id.dayThreeTemperatureTextView);
        forecastInfo[2][2] = view.findViewById(R.id.dayThreeWeatherTextView);
        weatherViews[2] = view.findViewById(R.id.viewWeatherDayThreeImageView);
    }

    /**
     * Sets all the fonts to the Hero List font
     */
    private void initializeFonts() {
        android.graphics.Typeface custom_font = android.graphics.Typeface.createFromAsset(getActivity().getAssets(), "fonts/Hero Light.otf");

        temperature.setTypeface(custom_font);
        location.setTypeface(custom_font);
        maxAndMin.setTypeface(custom_font);
        weather.setTypeface(custom_font);

        for (int i = 0; i < forecastInfo.length; i++) {
            for (int j = 0; j < forecastInfo[i].length; j++) {
                forecastInfo[i][j].setTypeface(custom_font);
            }
        }
    }
}
