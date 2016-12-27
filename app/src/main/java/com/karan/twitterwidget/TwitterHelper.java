package com.karan.twitterwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by stpl on 12/27/2016.
 */

public class TwitterHelper  extends AsyncTask<Void,Void,Void>{
   private Configuration configuration;
    private TwitterFactory twitterFactory;
    private Twitter twitter;
    private RequestToken requestToken;
    private String TWITTER_CALLBACK_URL="aevie.com";
    private Context context;
    private RemoteViews remoteViews;
    private int WidgetID;
    private AppWidgetManager WidgetManager;

    TwitterHelper(Context context, RemoteViews views,int WidgetId,AppWidgetManager manager)
    {

        remoteViews=views;
        this.context=context;
        configuration=new ConfigurationBuilder().setOAuthConsumerSecret(Utility.Consumer_Secret).setOAuthConsumerKey(Utility.Consumer_KEY).build();
        twitterFactory=new TwitterFactory(configuration);
        twitter=twitterFactory.getInstance();
    }


    @Override
    protected Void doInBackground(Void... params) {

            try {
                requestToken=twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));

            } catch (TwitterException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("end","here");
        remoteViews.setTextViewText(R.id.login,"HI");
    }
}
