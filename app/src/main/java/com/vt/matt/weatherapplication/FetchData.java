package com.vt.matt.weatherapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FetchData extends AsyncTask<Void, Void, Void> {

    private String JSONUrl;
    private String JSONForecastURL = "";

    private String oneDayForecastData = "";
    private String threeDayForecastData = "";
    private String city;
    private static boolean validLocation;

    //contains all of the forecast objects to display the info
    //about the forecasts, this data will all be put into the views
    private NextDayForecast[] forecasts;

    private static final String API_KEY = "&APPID=29231a5e1cf868dea895af67d4ec9584";
    private static final String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String BASE_IMAGE_URL = "http://openweathermap.org/img/w/";
    private static final String BASE_IMAGE_URL_EXTENSION = ".png";
    private static final String TAG = "Fetch data";
    private static final String FILE_NAME = "cities.txt";

    //initialize urls
    public FetchData(String city) {
        super();
        this.city = city;
        this.JSONUrl = BASE_URL + "?q=" + this.city + "" + API_KEY;
        this.JSONForecastURL = BASE_FORECAST_URL + this.city + API_KEY;
        this.forecasts = new NextDayForecast[4];
        validLocation = true;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //if we have not seen this location before,
        if (!LocationsListFragment.sameNames(this.city, LocationsListFragment.locationsList)) {
            //check if the location is a valid location
            if (readInData(JSONUrl) == null) {
                validLocation = false;
            }
        }

        //if the location is valid, read in all of its data to the forecasts array
        if (validLocation) {
            //get current forecast
            this.oneDayForecastData = readInData(JSONUrl);
            this.threeDayForecastData = readInData(JSONForecastURL);

            //read in data for the forecast
            try {
                JSONObject root = new JSONObject(oneDayForecastData);
                JSONArray weather = root.getJSONArray("weather");
                JSONObject main = root.getJSONObject("main");
                JSONObject condition = weather.getJSONObject(0);

                //parse the JSON object
                int temperature = kelvinToFahrenheit(Double.parseDouble(main.getString("temp")));
                int temp = temperature;
                int high = kelvinToFahrenheit(Double.parseDouble(main.getString("temp_max")));
                int low = kelvinToFahrenheit(Double.parseDouble(main.getString("temp_min")));
                String weatherType = condition.getString("description");
                String icon = condition.getString("icon");

                JSONObject forecastRoot = new JSONObject(threeDayForecastData);
                JSONArray dailyForecasts = forecastRoot.getJSONArray("list");

                //add the forecast for the current day into the forecast array
                NextDayForecast mainForecast = new NextDayForecast(
                        temp, low, high, weatherType, "date", getIconURL(icon), this.city
                );
                forecasts[0] = mainForecast;

                //sets up the three day forecast
                this.initNextDayForecasts(dailyForecasts);

            } catch (JSONException e) {
                validLocation = false;
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (validLocation) {
            //if this location has not been seen, add the location to teh locations list and save it to the file
            if (!LocationsListFragment.sameNames(this.city, LocationsListFragment.locationsList)) {
                LocationsListFragment.locationsList.add(new Location(this.city, 70, false));
                Toast.makeText(MainActivity.context, "Location saved", Toast.LENGTH_SHORT).show();
                save();
            }

            //set text view and images for the current forecast
            ForecastFragment.location.setText(forecasts[0].getLocation());
            ForecastFragment.temperature.setText(forecasts[0].getTemperature() + "°");
            ForecastFragment.maxAndMin.setText("" + forecasts[0].getTempMin() + "°" + " / " + forecasts[0].getTempHigh() + "°F");
            ForecastFragment.weather.setText(forecasts[0].getWeather());
            Picasso.get().load(forecasts[0].getUrl()).into(ForecastFragment.viewWeather);

            //set text views for the 3 day forecast
            for (int i = 0; i < 3; i++) {
                NextDayForecast currDay = forecasts[i + 1];
                ForecastFragment.forecastInfo[i][0].setText(currDay.getDate());
                ForecastFragment.forecastInfo[i][1].setText("" + currDay.getTemperature() + "°");
                ForecastFragment.forecastInfo[i][2].setText(currDay.getWeather());
                Picasso.get().load(currDay.getUrl()).into(ForecastFragment.weatherViews[i]);
            }

            LocationsListFragment.locationsList.get(indexOf(forecasts[0].getLocation(), LocationsListFragment.locationsList)).setTemp(forecasts[0].getTemperature());
            LocationsListFragment.adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(MainActivity.context, "Please Enter a Valid Location...", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Reads in the data for the three day forecast
     * @param baseURL The url to read in the JSON object from
     * @return a String containing all of the JSON objects from the url
     */
    private String readInData(String baseURL) {
        String urlData = "";
        try {
            URL url = new URL(baseURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //read the json file
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                urlData += line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        }
        return urlData;
    }

    /*
     * Initalizes the three day forecast
     * @param dailyForecasts The JSONArray that contains the three day forecast data
     */
    private void initNextDayForecasts(JSONArray dailyForecasts) {
        try {
            int numDays = 0;
            int timeCount = 0;
            int temp = 0;
            int min = 0;
            int max = 0;
            String date = "";

            HashMap<String, Integer> weatherDescCounts = new HashMap<>();
            String desc;

            HashMap<String, Integer> weatherTypeCounts = new HashMap<>();
            String mainType;

            for (int i = 0; i < dailyForecasts.length(); i++) {
                if (numDays < 3) {
                    int currentDay = Integer.parseInt(dailyForecasts.getJSONObject(i).getString("dt_txt").substring(8, 10));
                    //if we have not reached a new day in the forecast
                    if (i == 0 || currentDay == Integer.parseInt(dailyForecasts.getJSONObject(i - 1).getString("dt_txt").substring(8, 10))) {
                        JSONObject temps = dailyForecasts.getJSONObject(i).getJSONObject("main");
                        JSONObject mainWeather = dailyForecasts.getJSONObject(i).getJSONArray("weather").getJSONObject(0);

                        desc = mainWeather.getString("description");
                        mainType = mainWeather.getString("main");
                        temp += kelvinToFahrenheit(temps.getDouble("temp"));
                        min += kelvinToFahrenheit(temps.getDouble("temp_min"));
                        max += kelvinToFahrenheit(temps.getDouble("temp_max"));
                        date = dailyForecasts.getJSONObject(i).getString("dt_txt").substring(0, 10);
                        date = formatDate(date);

                        //if we have not seen this type of weather, add it to the map
                        if (!weatherDescCounts.containsKey(desc)) {
                            weatherDescCounts.put(desc, 1);
                        } else {
                            //increment the number of times this description has been seen
                            weatherDescCounts.put(desc, weatherDescCounts.get(desc) + 1);
                        }

                        if (!weatherTypeCounts.containsKey(mainType)) {
                            weatherTypeCounts.put(mainType, 1);
                        } else {
                            weatherTypeCounts.put(mainType, weatherTypeCounts.get(mainType) + 1);
                        }

                        timeCount++;
                    } else {
                        //if we have reached a new day, record the previous day's average temp, high, min ect..
                        temp /= timeCount;
                        min /= timeCount;
                        max /= timeCount;

                        int highest = -1;
                        String highestKey = "";
                        for (Object key : weatherDescCounts.keySet()) {
                            String weatherDesc = (String) key;
                            if (weatherDescCounts.get(weatherDesc) > highest) {
                                highest = weatherDescCounts.get(weatherDesc);
                                highestKey = weatherDesc;
                            }
                        }
                        weatherDescCounts.clear();

                        int highestType = -1;
                        String highestTypeKey = "";
                        for (Object key : weatherTypeCounts.keySet()) {
                            String weatherType = (String) key;
                            if (weatherTypeCounts.get(weatherType) > highestType) {
                                highestType = weatherTypeCounts.get(weatherType);
                                highestTypeKey = weatherType;
                            }
                        }
                        weatherTypeCounts.clear();

                        NextDayForecast nextDayForecast = new NextDayForecast(
                                temp, min, max, highestKey, date, getIconURL(getIconFromType(highestTypeKey)), this.city
                        );
                        forecasts[numDays + 1] = nextDayForecast;

                        temp = 0;
                        min = 0;
                        max = 0;
                        timeCount = 0;
                        numDays++;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the icon url
     * @param icon The icon ID (01d, 02d, 03d...)
     * @return The full url to the icon image
     */
    private String getIconURL(String icon) {
        return BASE_IMAGE_URL + icon + BASE_IMAGE_URL_EXTENSION;
    }

    /**
     * Converts a kelvin temperature to fahrenheit
     * @param kTemp The kelvin temp to convert
     * @return The fahrenheit equivalent of kTemp
     */
    private int kelvinToFahrenheit(double kTemp) {
        return (int) (1.8 * (kTemp - 273) + 32);
    }

    /**
     * Formats a date of the form YYYY-MM-DD to the correct day of the week
     * @param dayDate The date in form YYYY-MM-DD
     * @return The day of the week dayDate occurs on
     */
    private String formatDate(String dayDate) {
        String newDate = "";
        try {
            SimpleDateFormat formatFirst = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatFirst.parse(dayDate);
            Log.d(TAG, date.toString());
            DateFormat format = new SimpleDateFormat("EEEE");
            newDate = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;

    }

    /**
     * Converts a type of weather to the correct icon
     * @param type The type of weather to convert
     * @return The icon id (01d, 02d, 03d...)
     */
    private String getIconFromType(String type) {
        String icon = "";
        switch (type) {
            case "Clear":
                icon = "01d";
                break;
            case "Clouds":
                icon = "03d";
                break;
            case "Rain":
                icon = "10d";
                break;
            case "Snow":
                icon = "13d";
                break;

            default:
                icon = "09d";
        }
        return icon;
    }

    /**
     * Gets the index of a location in the locations array
     * @param loc The name of the location to determine the index of
     * @param locations The array to search for location
     * @return The index of loc in locations, -1 if loc is not present
     */
    private int indexOf(String loc, ArrayList<Location> locations) {
        Log.v(TAG, "Serarching for..." + loc);
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getName().toLowerCase().equals(loc.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Saves the city of the FetchData object into the cities.txt file
     */
    public void save() {
        String text = this.city;
        StringBuilder sb = load();
        FileOutputStream fos = null;

        try {
            fos = MainActivity.context.openFileOutput(FILE_NAME, MainActivity.context.MODE_PRIVATE);
            sb.append(text + "\n");
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

    /**
     * Loads all of the locations from the cities.txt file
     * @return A string containing all of the lcoations in cities.txt
     *         separated by "\n"
     */
    public static StringBuilder load() {
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();

        try {
            fis = MainActivity.context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb;
    }
}