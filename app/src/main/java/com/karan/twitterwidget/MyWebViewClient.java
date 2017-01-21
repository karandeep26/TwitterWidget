package com.karan.twitterwidget;

import android.net.Uri;
import android.webkit.WebViewClient;

import com.karan.twitterwidget.activity.WebViewActivity;
import com.karan.twitterwidget.asyncTasks.LoadPlaces;
import com.karan.twitterwidget.model.Country;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stpl on 12/28/2016.
 */

public class MyWebViewClient extends WebViewClient {
    private WebViewActivity activity;
    private HashMap<String, ArrayList<Country.City>> map = new HashMap<>();


    public MyWebViewClient(WebViewActivity webViewActivity) {
        activity = webViewActivity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
        if (!url.contains("http://aevie.com"))
            return false;
        Utility.oauthVerifier = Uri.parse(url).getQueryParameter("oauth_verifier");
        new LoadPlaces(activity).execute();
        return true;
    }


}
