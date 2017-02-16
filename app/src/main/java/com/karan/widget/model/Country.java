package com.karan.widget.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by stpl on 12/29/2016.
 */

public class Country implements Serializable {
    private String countryName;
    private int woeid;
    private ArrayList<City> cities;

    public Country(String countryName, int woeid) {
        this.countryName = countryName;
        this.woeid = woeid;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getWoeid() {
        return woeid;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    static public class City implements Serializable {
        String cityName;
        int woeid;

        public City(String cityName, int woeid) {
            this.cityName = cityName;
            this.woeid = woeid;
        }

        public String getCityName() {
            return cityName;
        }

        public int getWoeid() {
            return woeid;
        }
    }
}
