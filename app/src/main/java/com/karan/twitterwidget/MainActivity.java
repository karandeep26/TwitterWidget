package com.karan.twitterwidget;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {
    private Configuration configuration;
    private TwitterFactory twitterFactory;
    private Twitter twitter;
    private RequestToken requestToken;
    private String TWITTER_CALLBACK_URL="http://aevie.com";
    static final String URL_TWITTER_AUTH = "https://api.twitter.com/oauth/authorize";
    static final String URL_TWITTER_OAUTH_VERIFIER = "https://api.twitter.com/oauth/access_token";
    static final String URL_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/request_token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConfigurationBuilder configurationBuilder=new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Utility.Consumer_KEY);
        configurationBuilder.setOAuthConsumerSecret(Utility.Consumer_Secret);
        configuration=configurationBuilder.build();
        twitterFactory=new TwitterFactory(configuration);
        twitter=twitterFactory.getInstance();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestToken=twitter.getOAuthRequestToken("oauth_verifier");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

//        new TwitterHelper(this).execute();
    }
}
