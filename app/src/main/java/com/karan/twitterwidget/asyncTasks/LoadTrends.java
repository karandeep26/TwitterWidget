package com.karan.twitterwidget.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.activity.DialogActivity;
import com.karan.twitterwidget.model.TrendsList;
import com.karan.twitterwidget.widget.MyWidgetProvider;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class LoadTrends extends AsyncTask<Void, Void, Boolean> {
    private int woeid, widgetId;
    private Context context;
    private ArrayList<String> list;
    private String cityName;
    private boolean isRunning;

    public LoadTrends(Context context, int woeid, int widgetId, String name) {
        this.context = context;
        this.woeid = woeid;
        this.widgetId = widgetId;
        cityName = name;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean error = false;
        isRunning = true;
        try {
            Utility.twitter = Utility.getTwitterInstance();
            Utility.accessToken = new AccessToken(Utility.getSharedPreferences(context).getString(Utility.PREF_KEY_OAUTH_TOKEN, ""), Utility.getSharedPreferences(context).getString(Utility.PREF_KEY_OAUTH_SECRET, ""));
            Utility.twitter.setOAuthAccessToken(Utility.accessToken);
            Trends trends = Utility.twitter.getPlaceTrends(woeid);
            Trend trend[] = trends.getTrends();
            list = new ArrayList<>();
            for (Trend temp : trend) {
                list.add(temp.getName());
            }
        } catch (TwitterException e) {
            error = true;
        }
        return error;
    }

    @Override
    protected void onPostExecute(Boolean error) {
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.putExtra("WOEID", woeid);
        intent.putExtra("cityName", cityName);

        if (!error) {
            Utility.dataOfWidget.put(widgetId, new ArrayList<>(list));
            try {
                FileOutputStream outputStream = context.openFileOutput(widgetId + "list", Context.MODE_PRIVATE);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(new TrendsList(Utility.dataOfWidget.get(widgetId)));
                objectOutputStream.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            intent.setAction("load trends" + widgetId);
        } else {
            intent.setAction("noData" + widgetId);
        }
        context.sendBroadcast(intent);
        if (context instanceof DialogActivity)
            ((DialogActivity) context).finish();

    }

    public boolean isRunning() {
        return isRunning;
    }
}
