package com.karan.twitterwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import com.karan.twitterwidget.Activity.WebViewActivity;
import com.karan.twitterwidget.Model.Country;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by stpl on 12/27/2016.
 */

public class Utility {
    private static String Consumer_KEY="0MGmT3JDbhR3bnG3kkL129zrZ";
    private static String Consumer_Secret="FCgwYIIfcFsmqztC4WuwXmNToTL3nEqIeNQfILqRjV0KWj4W4c";
    static public String PREF_KEY_OAUTH_TOKEN="oauth_token";
    static public String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static public String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static public Twitter twitter;
    static public RequestToken requestToken;
    private static ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    static private Configuration configuration;
    static public AccessToken accessToken;
    static public String oauthVerifier;
    static public HashMap<Integer,ArrayList<String>> dataOfWidget = new HashMap<>();
    static public ArrayList<Country> countryList=new ArrayList<>();
    static public SharedPreferences sharedPreferences;

    static private Configuration buildConfiguration()
    {
        if(configuration==null)
        {
            configurationBuilder.setOAuthConsumerKey(Utility.Consumer_KEY);
            configurationBuilder.setOAuthConsumerSecret(Utility.Consumer_Secret);
            configuration= configurationBuilder.build();
        }
        return configuration;


    }
    static public Twitter getTwitterInstance()
    {
        if(twitter==null)
        {
            twitter= new TwitterFactory(buildConfiguration()).getInstance();

        }

            return twitter;
    }
    static public boolean isTwitterLoggedIn(Context context)
    {
       if(sharedPreferences==null)
           sharedPreferences=getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN,false);
    }
    static public SharedPreferences getSharedPreferences(Context context)
    {
        Log.i("context",context+"");
        sharedPreferences=context.getSharedPreferences("MyPref",0);
        return sharedPreferences;
    }


}
