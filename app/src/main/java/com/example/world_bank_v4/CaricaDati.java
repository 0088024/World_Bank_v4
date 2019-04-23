package com.example.world_bank_v4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class CaricaDati extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carica_dati);
         /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return false;
    }

}
