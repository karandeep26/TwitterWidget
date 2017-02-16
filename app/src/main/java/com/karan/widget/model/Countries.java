package com.karan.widget.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by stpl on 1/6/2017.
 */

public class Countries implements Serializable {
    ArrayList<Country> countries;

    public Countries(ArrayList<Country> CountryList) {
        this.countries = CountryList;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }
}
