package com.karan.twitterwidget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.karan.twitterwidget.ConnectionDetector;
import com.karan.twitterwidget.NothingSelectedSpinnerAdapter;
import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.asyncTasks.LoadTrends;
import com.karan.twitterwidget.model.Country;
import com.karan.twitterwidget.widget.MyWidgetProvider;

import java.util.ArrayList;

public class DialogActivity extends Activity {
    int countryId;
    int cityId;
    int woeid;
    int widgetId;
    ArrayAdapter<String> cityAdapter;
    ArrayList<String> cityNames = new ArrayList<>();
    Spinner citySpinner;
    private ConnectionDetector cd;
    private LoadTrends loadTrends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        widgetId = getIntent().getIntExtra("WidgetId", -1);
        Boolean error = getIntent().getBooleanExtra("error", false);
        Spinner countrySpinner = (Spinner) findViewById(R.id.country_spinner);

        if (error) {
            onError();
        }
        getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
        cd = new ConnectionDetector(getApplicationContext());
        Button button = (Button) findViewById(R.id.done);
        button.setOnClickListener(v -> {
            if (!cd.isConnectingToInternet()) {
                Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (cityNames.size() > 1 && citySpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select city", Toast.LENGTH_SHORT).show();
            } else if (countrySpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select country", Toast.LENGTH_SHORT).show();
            } else {
                if (loadTrends == null) {
                    loadTrends = new LoadTrends(DialogActivity.this, getWoeid(), getWidgetId(), getName());

                }
                if (!loadTrends.isRunning())
                    loadTrends.execute();
            }
        });
        countrySpinner.setPrompt("Select Country");
        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        citySpinner.setPrompt("Select City");
        ArrayList<String> countryNames = new ArrayList<>();
        for (Country country : Utility.countryList) {
            countryNames.add(country.getCountryName());
        }
        cityNames.add("ss");
        final ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryNames);
        countrySpinner.setAdapter(new NothingSelectedSpinnerAdapter(countryAdapter, R.layout.spinner_nothing_selected, this));
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cityNames);
        citySpinner.setAdapter(new NothingSelectedSpinnerAdapter(cityAdapter, R.layout.spinner_nothing_selected, this));
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryId = position - 1;

                if (position > 0) {
                    ArrayList<Country.City> cities = Utility.countryList.get(position - 1).getCities();
                    cityNames.clear();
                    if (cities != null) {
                        for (Country.City city : cities) {
                            cityNames.add(city.getCityName());
                        }
                    }
                    if (cityNames.size() > 0) {
                        citySpinner.setSelection(0);
                        citySpinner.setEnabled(true);
                        citySpinner.performClick();
                    } else {
                        cityNames.add("DUMMY");
                        citySpinner.setSelection(0);
                        citySpinner.setEnabled(false);

                    }
                } else {
                    citySpinner.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = position - 1;
                if (cityId > 0) {
                    woeid = getWoeid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public int getWoeid() {
        if (Utility.countryList.get(countryId).getCities() == null || Utility.countryList.get(countryId).getCities().
                get(cityId).getWoeid() == -1) {
            return Utility.countryList.get(countryId).getWoeid();
        }
        return Utility.countryList.get(countryId).getCities().get(cityId).getWoeid();
    }

    public String getName() {
        Country country = Utility.countryList.get(countryId);
        if (country.getCities() == null || country.getCities().get(cityId).getWoeid() == -1) {
            return Utility.countryList.get(countryId).getCountryName();
        }
        return country.getCities().get(cityId).getCityName();
    }

    public int getWidgetId() {
        return widgetId;
    }

    void onError() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction("Login Screen" + widgetId);
        sendBroadcast(intent);
        finish();
        Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadTrends != null && loadTrends.isRunning())
            loadTrends.cancel(true);

    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
