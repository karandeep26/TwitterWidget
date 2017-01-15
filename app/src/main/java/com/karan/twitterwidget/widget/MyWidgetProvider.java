package com.karan.twitterwidget.widget;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.karan.twitterwidget.ConnectionDetector;
import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.activity.DialogActivity;
import com.karan.twitterwidget.activity.WebViewActivity;
import com.karan.twitterwidget.asyncTasks.LoadTrends;
import com.karan.twitterwidget.model.Countries;

import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class MyWidgetProvider extends AppWidgetProvider {
    RemoteViews views;
    int widgetID;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views, int appWidgetId, int WOEID, String cityName) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class).setData(Uri.fromParts("content", String.valueOf(appWidgetId), null)).putExtra("WOEID", WOEID).putExtra("cityName", cityName));
    }

    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        int WOEID = -1;

        views = new RemoteViews(context.getPackageName(), R.layout.login_widget);
        if (Utility.isTwitterLoggedIn(context)) {
            views = new RemoteViews(context.getPackageName(), R.layout.logged_in_layout);
            views.setOnClickPendingIntent(R.id.more, getPendingSelfIntent(context, "more" + appWidgetId, WOEID, "null"));
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.login_widget);
            views.setOnClickPendingIntent(R.id.login, getPendingSelfIntent(context, "on login" + appWidgetId, WOEID, "null"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views, appWidgetId, 0, "null");
        } else {
            setRemoteAdapterV11(context, views);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String cityName = null;
        String action = intent.getAction();
        ConnectionDetector connectionDetector = new ConnectionDetector(context);
        int WOEID = intent.getIntExtra("WOEID", -1);
        if (intent.hasExtra("cityName"))
            cityName = intent.getExtras().getString("cityName", "null");
        if (action.startsWith("on login")) {
            onLogin(connectionDetector, action, context, WOEID);
        } else if (action.startsWith("load trends")) {
            loadTrends(action, context, WOEID, cityName);
        } else if (action.startsWith("more")) {
            onSettings(action, context);
        } else if (action.startsWith("Login Screen")) {
            moveToLogin(action, context);
        } else if (action.startsWith("on click")) {
            onListViewItemClick(intent, context);
        } else if (action.startsWith("refresh")) {
            refreshList(connectionDetector, context, action, WOEID, cityName);
        } else if (action.startsWith("noData")) {
            noDataToShow(context, action, WOEID, cityName, intent);
        } else if (action.startsWith("loggedIn")) {
            loggedIn(context, action);
        } else if (action.startsWith("loading")) {
            widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.loading_layout);
            AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
        }
    }

    private void loggedIn(Context context, String action) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.logged_in_layout);
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        view.setOnClickPendingIntent(R.id.more, getPendingSelfIntent(context, "more" + widgetID, -1, "null"));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, view);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int WOEID, String city) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("WOEID", WOEID);
        intent.putExtra("cityName", city);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void onLogin(ConnectionDetector connectionDetector, String action, Context context, int WOEID) {
        if (!connectionDetector.isConnectingToInternet()) {
            Toast.makeText(context, "not connection to Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        views = new RemoteViews(context.getPackageName(), R.layout.logged_in_layout);
        views.setOnClickPendingIntent(R.id.more, getPendingSelfIntent(context, "more" + widgetID, WOEID, "null"));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
        Intent webViewActivity = new Intent(context, WebViewActivity.class);
        webViewActivity.putExtra("WidgetId", widgetID);
        webViewActivity.putExtra("WOEID", WOEID);
        webViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(webViewActivity);
    }

    private void loadTrends(String action, Context context, int WOEID, String cityName) {
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.title, cityName);
        views.setOnClickPendingIntent(R.id.more, getPendingSelfIntent(context, "more" + widgetID, widgetID, cityName));
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.widget_list);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
        setRemoteAdapter(context, views, widgetID, WOEID, cityName);
            /* Refresh button handling*/
        views.setOnClickPendingIntent(R.id.refresh, getPendingSelfIntent(context, "refresh" + widgetID, WOEID, cityName));
        Log.d("REFRESH", "****" + WOEID);
            /* Handle OnClick on ListView Item */
        Intent listViewClickIntent = new Intent(context, MyWidgetProvider.class);
        listViewClickIntent.setAction("on click");
        listViewClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent listViewItemPendingIntent = PendingIntent.getBroadcast(context, 0, listViewClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, listViewItemPendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
    }

    private void onSettings(String action, Context context) {
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        if (Utility.countryList == null || Utility.countryList.size() == 0) {
            FileInputStream fileInputStream;
            try {
                fileInputStream = context.openFileInput("Locations");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Utility.countryList = ((Countries) objectInputStream.readObject()).getCountries();
            } catch (java.io.IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        Intent launchMore = new Intent(context, DialogActivity.class);
        launchMore.putExtra("WidgetId", widgetID);
        launchMore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchMore);
    }

    private void moveToLogin(String action, Context context) {
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.login_widget);
        views.setOnClickPendingIntent(R.id.login, getPendingSelfIntent(context, "on login" + widgetID, 0, "null"));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
    }

    private void onListViewItemClick(Intent intent, Context context) {
        String trend = intent.getExtras().getString("trend");
        if (trend != null && trend.contains("#"))
            trend = trend.replace("#", "%23");
        try {
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://search?query=" + trend));
            twitterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(twitterIntent);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/search?f=realtime&q=" + trend)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void refreshList(ConnectionDetector connectionDetector, Context context, String action, int WOEID, String cityName) {
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        if (!connectionDetector.isConnectingToInternet()) {
            Toast.makeText(context, "Not connected to Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.loading_layout);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        new LoadTrends(context, WOEID, widgetID, cityName).execute();

    }

    private void noDataToShow(Context context, String action, int WOEID, String cityName, Intent intent) {
        widgetID = Integer.parseInt(action.replaceAll("[^0-9]", ""));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.no_data_to_load);
        views.setOnClickPendingIntent(R.id.more, getPendingSelfIntent(context, "more" + widgetID, WOEID, intent.getStringExtra("cityName")));
        views.setOnClickPendingIntent(R.id.refresh, getPendingSelfIntent(context, "refresh" + widgetID, WOEID, cityName));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, views);
    }


}
