package com.example.world_bank_v4.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView textnot;
    private Intent intent_prec;
    private Bundle bundle_prec;
    private String message;
    private Button button_ricarica_pagina;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": CREATE");


        getSupportActionBar().setLogo(R.drawable.unreach);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textnot = findViewById(R.id.textViewNotification);
        button_ricarica_pagina = findViewById(R.id.button_ricarica_pagina);

        button_ricarica_pagina.setOnClickListener(this);

        intent_prec = getIntent();      /*ritorna l'intento che ha avviato questa activity*/
        bundle_prec = intent_prec.getExtras();
        message = bundle_prec.getString("error");
        textnot.setText(bundle_prec.getString("risulato", message));
    }


    public void onClick(View v) {
        setResult(Costanti.RETURN_FROM_NOTIFICATION_ACTIVITY);
        finish();

    }


}

