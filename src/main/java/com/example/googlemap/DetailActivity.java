package com.example.googlemap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        String url =  "http://place.map.daum.net/"+id;
        WebView wv = (WebView)findViewById(R.id.webView);
        WebViewClient wc = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
        wv.setWebViewClient(wc);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(url);
    }
}
