package com.karan.widget.widget;

/**
 * Created by stpl on 12/27/2016.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by stpl on 12/27/2016.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}