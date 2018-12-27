package com.vt.matt.weatherapplication;

/**
 * Simple class to represent a location
 */
public class Location {
    private String name;
    private int temp;
    private boolean selected;

    public Location(String name, int temp, boolean selected) {
        this.name = name;
        this.temp = temp;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) { this.temp = temp; }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
