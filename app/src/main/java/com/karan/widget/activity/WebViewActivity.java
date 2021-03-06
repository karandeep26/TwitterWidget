package com.karan.widget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;

import com.karan.widget.AlertDialogManager;
import com.karan.widget.ConnectionDetector;
import com.karan.widget.MyWebViewClient;
import com.karan.widget.R;
import com.karan.widget.Utility;
import com.karan.widget.widget.MyWidgetProvider;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class WebViewActivity extends Activity {
    MyWebViewClient webViewClient;
    AlertDialogManager alert = new AlertDialogManager();
    private WebView webView;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        id = getIntent().getIntExtra("WidgetId", -1);
        webView = (WebView) findViewById(R.id.webView);
        webViewClient = new MyWebViewClient(this);
        webView.setWebViewClient(webViewClient);
        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                onError();
            }
            return false;
        });
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
                WebViewActivity.this.onError();
            }

            @Override
            public void onNext(RequestToken requestToken) {
                if (requestToken.getAuthenticationURL() != null) {
                    webView.loadUrl(requestToken.getAuthenticationURL());
                } else {
                    onError(new Throwable());
                }
            }
        });


    }

    public int getWidgetId() {
        return id;
    }

    public void onError() {
        Intent intent = new Intent(WebViewActivity.this, MyWidgetProvider.class);
        intent.setAction("Login Screen" + id);
        WebViewActivity.this.sendBroadcast(intent);
        finish();
        Toast.makeText(WebViewActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
    }
}