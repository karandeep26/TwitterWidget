package com.karan.twitterwidget.Activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

import com.karan.twitterwidget.AlertDialogManager;
import com.karan.twitterwidget.AsyncTasks.LoadTrends;
import com.karan.twitterwidget.ConnectionDetector;
import com.karan.twitterwidget.Model.Country;
import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;

import java.io.UTFDataFormatException;
import java.util.ArrayList;

public class DialogActivity extends Activity {
    int countryid;
    int cityId;
    int woeid;
    int widgetId;
    AlertDialogManager alert = new AlertDialogManager();
    private ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        widgetId=getIntent().getIntExtra("WidgetId",-1);
        getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
        cd = new ConnectionDetector(getApplicationContext());
        Button button= (Button) findViewById(R.id.done);
        button.setOnClickListener(v -> {
            if(!cd.isConnectingToInternet()){
                Toast.makeText(this,"No Connection",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            new LoadTrends(DialogActivity.this, getWoeid(), getWidgetId(), getName()).execute();
        });
        Spinner countrySpinner= (Spinner) findViewById(R.id.country_spinner);
        countrySpinner.setPrompt("Select Country");
        final Spinner citySpinner=(Spinner)findViewById(R.id.city_spinner);
        citySpinner.setPrompt("Select City");
        ArrayList<String> countryNames=new ArrayList<>();
        for(Country country:Utility.countryList){
            countryNames.add(country.getCountryName());
        }
        final ArrayAdapter<String> countryAdapter= new ArrayAdapter<>(this, R.layout.spinner_item, countryNames);
        countrySpinner.setAdapter(countryAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryid=position;
                ArrayList<Country.City> cities=Utility.countryList.get(position).getCities();
                ArrayList<String> cityNames=new ArrayList<String>();
                if(cities!=null) {
                    for (Country.City city : cities) {
                        cityNames.add(city.getCityName());
                    }
                }
                ArrayAdapter<String> cityAdapter=new ArrayAdapter<>(DialogActivity.this,R.layout.spinner_item,cityNames);
                citySpinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId=position;
                woeid= getWoeid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public int getWoeid(){
        if(Utility.countryList.get(countryid).getCities()==null){
            return Utility.countryList.get(countryid).getWoeid();
        }
        return Utility.countryList.get(countryid).getCities().get(cityId).getWoeid();

    }
   public String getName(){
       if(Utility.countryList.get(countryid).getCities()==null){
           return Utility.countryList.get(countryid).getCountryName();
       }
        return Utility.countryList.get(countryid).getCities().get(cityId).getCityName();}
    public int getWidgetId()
    {
        return widgetId;
    }
}
