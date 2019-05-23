package com.example.world_bank_v4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;



public class WebActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        WebView webView = findViewById((R.id.webView));

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        webView.loadUrl(appLinkData.toString());

    }

}
