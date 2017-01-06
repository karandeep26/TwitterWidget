package com.karan.twitterwidget.Widget;

/**
 * Created by stpl on 12/27/2016.
 */
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by stpl on 12/27/2016.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Widget","service");
        return new WidgetDataProvider(this, intent);
    }
}