package com.vt.matt.weatherapplication;

/**
 * Class to represent one of the days of the three day forecast
 */
public class NextDayForecast {

    private int temperature;
    private int tempMin;
    private int tempHigh;
    private String weather;
    private String date;
    private String url;
    private String location;

    public NextDayForecast(int temperature, int tempMin, int tempHigh, String weather, String date, String url, String location) {
        this.temperature = temperature;
        this.tempMin = tempMin;
        this.tempHigh = tempHigh;
        this.weather = weather;
        this.date = date;
        this.url = url;
        this.location = location;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getTempMin() {
        return tempMin;
    }

    public int getTempHigh() {
        return tempHigh;
    }

    public String getWeather() {
        return weather;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return this.location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}