package com.karan.twitterwidget.widget;

/**
 * Created by stpl on 12/27/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.model.TrendsList;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;


class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private int appWidgetId;
    private List<String> mCollection = new ArrayList<>();
    private Context mContext = null;

    WidgetDataProvider(Context context, Intent intent) {
        appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        Log.d("Constructor", "Called");
        if (Utility.dataOfWidget.get(appWidgetId) != null) {
            Log.d("Utility data", "not null");
            mCollection = new ArrayList<>(Utility.dataOfWidget.get(appWidgetId));
        }
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (Utility.dataOfWidget.get(appWidgetId) != null) {
            mCollection = new ArrayList<>(Utility.dataOfWidget.get(appWidgetId));
        }
        if (mCollection.size() == 0) {
            try {
                loadTrends();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
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
        String trend = mCollection.get(position);
        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putString("trend", trend);
        intent.putExtras(extras);
        view.setOnClickFillInIntent(android.R.id.text1, intent);
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
            FileInputStream fileInputStream = mContext.openFileInput(appWidgetId + "list");
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            TrendsList trendsList = (TrendsList) inputStream.readObject();
            mCollection = new ArrayList<>(trendsList.getList());
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
