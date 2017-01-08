package com.karan.twitterwidget.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.karan.twitterwidget.Activity.DialogActivity;
import com.karan.twitterwidget.Model.TrendsList;
import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.Widget.MyWidgetProvider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 */

public class LoadTrends extends AsyncTask<Void,Void,Void> {
    private int woeid,widgetId;
    private Context context;
    private ArrayList<String> list;
    private String cityName;
    public LoadTrends(Context context, int woeid, int widgetId, String name){
        this.context=context;
        this.woeid=woeid;
        this.widgetId=widgetId;
        cityName=name;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Utility.twitter=Utility.getTwitterInstance();
            Utility.accessToken = new AccessToken(Utility.getSharedPreferences(context).getString(Utility.PREF_KEY_OAUTH_TOKEN, null), Utility.getSharedPreferences(context).getString(Utility.PREF_KEY_OAUTH_SECRET, null));
            Utility.twitter.setOAuthAccessToken(Utility.accessToken);
            Trends trends= Utility.twitter.getPlaceTrends(woeid);
            Trend trend[]=trends.getTrends();
            list=new ArrayList<>();
            for(Trend temp:trend){
                list.add(temp.getName());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Utility.dataOfWidget.put(widgetId,new ArrayList<>(list));
        try {
            FileOutputStream outputStream=context.openFileOutput(widgetId+"list",Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(new TrendsList(Utility.dataOfWidget.get(widgetId)));
            objectOutputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction("load trends"+widgetId);
        intent.putExtra("WOEID",woeid);
        intent.putExtra("cityName",cityName);
        context.sendBroadcast(intent);
        if(context instanceof DialogActivity)
        ((DialogActivity)context).finish();
    }
}
