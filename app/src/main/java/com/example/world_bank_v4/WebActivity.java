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

        //WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);

    }


    /*se l'utente preme il pulsante indietro l'attività viene semplicemente terminata ed espulsa
    dallo stack activity, in modo da far tornare in 1°piano quella che l'aveva lanciata*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent();
        setResult(RESULT_OK,intent); // Informa l'attività chiamante che è tutto ok
        finish();
        return false;
    }*/

}
