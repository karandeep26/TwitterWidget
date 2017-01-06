package com.karan.twitterwidget.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.karan.twitterwidget.AlertDialogManager;
import com.karan.twitterwidget.AsyncTasks.LoadPlaces;
import com.karan.twitterwidget.ConnectionDetector;
import com.karan.twitterwidget.MyWebViewClient;
import com.karan.twitterwidget.R;
import com.karan.twitterwidget.Utility;
import com.karan.twitterwidget.Widget.MyWidgetProvider;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class WebViewActivity extends Activity {
    private WebView webView;
    MyWebViewClient webViewClient;
    AlertDialogManager alert = new AlertDialogManager();
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        id = getIntent().getIntExtra("WidgetId", -1);
        Log.d("WebView-Id", id + "");
        webView = (WebView) findViewById(R.id.webView);
        webViewClient = new MyWebViewClient(this);
        webView.setWebViewClient(webViewClient);
        Utility.sharedPreferences = getSharedPreferences("MyPref", 0);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            Intent intent = new Intent(this, MyWidgetProvider.class);
            intent.setAction("Login Screen" + id);
            sendBroadcast(intent);
            return;
        }
        if (!Utility.getSharedPreferences(this).getBoolean(Utility.PREF_KEY_TWITTER_LOGIN, false)) {
            Log.d("login", "false");
            Observable<RequestToken> requestTokenObservable = Observable.defer(() -> {
                try {
                    Utility.requestToken = Utility.getTwitterInstance().getOAuthRequestToken();
                } catch (TwitterException e) {
                    Observable.error(e);
                    e.printStackTrace();
                }
                return Observable.just(Utility.requestToken);
            });
            requestTokenObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<RequestToken>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(RequestToken requestToken) {
                    loadUrl(requestToken.getAuthenticationURL());
                }
            });
        } else {
            Log.d("login", "true");
            new LoadPlaces(this, 0).execute();
        }
    }

    public void loadUrl(String url) {
        if (url != null)
            webView.loadUrl(url);
        else
            Toast.makeText(this, "invalid url", Toast.LENGTH_SHORT).show();
    }

    public int getWidgetId() {
        return id;
    }
}
