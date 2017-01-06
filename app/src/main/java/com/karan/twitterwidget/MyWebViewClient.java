package com.karan.twitterwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebViewClient;

import com.karan.twitterwidget.Activity.DialogActivity;
import com.karan.twitterwidget.Activity.WebViewActivity;
import com.karan.twitterwidget.AsyncTasks.LoadPlaces;
import com.karan.twitterwidget.Model.Country;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import static com.karan.twitterwidget.Utility.accessToken;

/**
 * Created by stpl on 12/28/2016.
 */

public class MyWebViewClient extends WebViewClient{
    private WebViewActivity activity;
    private HashMap<String, ArrayList<Country.City>> map = new HashMap<>();
    private ArrayList<Country> countries = new ArrayList<>();
    ResponseList<Location> list = null;


    public MyWebViewClient(WebViewActivity webViewActivity)
    {
        activity=webViewActivity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
        if(!url.contains("http://aevie.com"))
            return false;
        Utility.oauthVerifier= Uri.parse(url).getQueryParameter("oauth_verifier");
        new LoadPlaces(activity,0).execute();
        return true;


    }
}
