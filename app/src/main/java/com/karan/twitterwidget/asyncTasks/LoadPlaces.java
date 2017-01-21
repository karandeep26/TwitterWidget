package com.karan.twitterwidget.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.activity.DialogActivity;
import com.karan.twitterwidget.activity.WebViewActivity;
import com.karan.twitterwidget.model.Countries;
import com.karan.twitterwidget.model.Country;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import static com.karan.twitterwidget.Utility.accessToken;
import static com.karan.twitterwidget.Utility.requestToken;


public class LoadPlaces extends AsyncTask<Void, Void, Boolean> {
    private Context context;

    public LoadPlaces(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HashMap<String, ArrayList<Country.City>> cityMap = new HashMap<>();
        Boolean error = false;
        try {
            accessToken = new AccessToken(requestToken.getToken(), requestToken.getTokenSecret());
            accessToken = Utility.getTwitterInstance().getOAuthAccessToken(requestToken, Utility.oauthVerifier);
            Utility.getTwitterInstance().setOAuthAccessToken(accessToken);
            ResponseList<Location> locationResponseList = Utility.getTwitterInstance().getAvailableTrends();
            for (Location location : locationResponseList) {
                if (location.getWoeid() == 1) {
                    Utility.countryList.add(new Country("World", location.getWoeid()));
                }
                if (location.getPlaceName() != null) {
                    if (location.getPlaceName().equalsIgnoreCase("town")) {
                        if (!cityMap.containsKey(location.getCountryCode())) {
                            ArrayList<Country.City> cityArrayList = new ArrayList<>();
                            cityArrayList.add(new Country.City("All", -1));
                            cityArrayList.add(new Country.City(location.getName(), location.getWoeid()));
                            cityMap.put(location.getCountryCode(), cityArrayList);
                        } else {
                            ArrayList<Country.City> value = cityMap.get(location.getCountryCode());
                            value.add(new Country.City(location.getName(), location.getWoeid()));
                            cityMap.put(location.getCountryCode(), value);
                        }
                    } else if (location.getPlaceName().equalsIgnoreCase("country")) {
                        Country country = new Country(location.getCountryName(), location.getWoeid());
                        country.setCities(cityMap.get(location.getCountryCode()));
                        Utility.countryList.add(country);
                    }
                } else {
                    Utility.countryList.add(new Country("WORLD", location.getWoeid()));
                }
            }
        } catch (TwitterException e) {
            error = true;
        }
        return error;
    }

    @Override
    protected void onPostExecute(Boolean error) {
        Intent intent = new Intent(context, DialogActivity.class);
        intent.putExtra("WidgetId", ((WebViewActivity) context).getWidgetId());
        intent.putExtra("error", error);
        if (!error) {
            Countries countries = new Countries(Utility.countryList);
            try {
                FileOutputStream fileOutput = context.openFileOutput("Locations", Context.MODE_PRIVATE);
                ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
                objectOutput.writeObject(countries);
                objectOutput.close();
                fileOutput.close();
                SharedPreferences.Editor editor = Utility.getSharedPreferences(context).edit();
                editor.putString(Utility.PREF_KEY_OAUTH_SECRET, Utility.accessToken.getTokenSecret());
                editor.putString(Utility.PREF_KEY_OAUTH_TOKEN, Utility.accessToken.getToken());
                editor.putBoolean(Utility.PREF_KEY_TWITTER_LOGIN, true);
                editor.apply();
                context.startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ((WebViewActivity) context).finish();

    }
}
