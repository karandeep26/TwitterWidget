package com.karan.twitterwidget.Widget;

/**
 * Created by stpl on 12/27/2016.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.karan.twitterwidget.ConnectionDetector;
import com.karan.twitterwidget.Model.TrendsList;
import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;



class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetDataProvider";
    private int appWidgetId;
    private List<String> mCollection = new ArrayList<>();
    private Context mContext = null;
    private int WOEID;
    private String city;


    WidgetDataProvider(Context context,Intent intent){
        appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        WOEID = intent.getIntExtra("WOEID", -1);
        city=intent.getExtras().getString("cityName","null");
        if(Utility.dataOfWidget.get(appWidgetId)!=null)
            mCollection=new ArrayList<>(Utility.dataOfWidget.get(appWidgetId));
        mContext=context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
       if(mCollection.size()==0)
           try {
               loadTrends();
           } catch (TwitterException e) {
               e.printStackTrace();
           }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.listview_row);
        view.setTextViewText(android.R.id.text1, mCollection.get(position));
        String trend=mCollection.get(position);
        Intent intent=new Intent();
        Bundle extras=new Bundle();
        extras.putString("trend",trend);
        intent.putExtras(extras);
        view.setOnClickFillInIntent(android.R.id.text1,intent);
        return view;
    }
    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.loading_row);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    private void loadTrends() throws TwitterException {
        try {
            FileInputStream fileInputStream=mContext.openFileInput(appWidgetId+"list");
            ObjectInputStream inputStream=new ObjectInputStream(fileInputStream);
            TrendsList trendsList= (TrendsList) inputStream.readObject();
            mCollection=trendsList.getList();
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
