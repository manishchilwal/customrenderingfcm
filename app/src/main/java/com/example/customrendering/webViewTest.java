package com.example.customrendering;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

public class webViewTest extends AppCompatActivity {

    CleverTapAPI cleverTapDefaultInstance;
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        browser = findViewById(R.id.webView);
        browser.setWebViewClient(new MyBrowser());
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new CTWebInterface(CleverTapAPI.getDefaultInstance(this)),"my_webview");
        browser.loadUrl("https://cttestnative.000webhostapp.com/");
    }

    private class MyBrowser extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}