package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.world_bank_v4.Dialog.DialogInfo;
import com.example.world_bank_v4.R;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    private ImageView imageView;
    private Button buttonPaese;
    private Button buttonArgomento;
    private Button buttonGrafico;
    private Button buttonDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": CREATE");

    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setLogo(R.drawable.wb_logo_home_dark);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": RESUME");

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.wb1);

        buttonPaese = findViewById(R.id.buttonPaese);
        buttonArgomento = findViewById(R.id.buttonArgomento);
        buttonGrafico = findViewById(R.id.buttonGrafico);
        buttonDatabase = findViewById(R.id.buttonDatabase);


        buttonPaese.setOnClickListener(this);
        buttonArgomento.setOnClickListener(this);
        buttonGrafico.setOnClickListener(this);
        buttonDatabase.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_di_scelta,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Resources res = getResources();
        Intent intent;
        Bundle bundle;
        DialogInfo mydialog;
        int id = item.getItemId();
        switch(id) {
            case R.id.Menu_1:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(res.getString(R.string.WORLDBANK_SITE)));
                /* If there are more than one browser installed, a window will appear to allow the user to choose what he prefers.*/
                startActivity(Intent.createChooser(intent,
                        res.getString(R.string.CHOOSE_BROWSER)));
                break;

            case R.id.Menu_2:
                mydialog = new DialogInfo();
                bundle = new Bundle();
                /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
                bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG),
                        res.getStringArray(R.array.stringhe_dialog_about_us));
                bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.about);
                bundle.putInt(res.getString(R.string.KEY_ID_LAYOUT), R.layout.about_layout);


                mydialog.setArguments(bundle);
                mydialog.show(getSupportFragmentManager(),
                        getResources().getString(R.string.MY_DIALOG));
                break;

            case R.id.Menu_3:
                mydialog = new DialogInfo();
                bundle = new Bundle();
                /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
                bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG),
                        res.getStringArray(R.array.stringhe_dialog_contacts));
                bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.logouni1);
                bundle.putInt(res.getString(R.string.KEY_ID_LAYOUT), R.layout.contacts_layout);

                mydialog.setArguments(bundle);
                mydialog.show(getSupportFragmentManager(),
                        getResources().getString(R.string.MY_DIALOG));
                break;

        }
        return false;
    }


    @Override
    public void onClick(View v) {
        Resources res = getResources();
        Intent intent;
        Bundle bundle;
        int id = v.getId();         /*ottengo l'id del bottone selezionato*/
        switch(id) {
            case R.id.buttonPaese:

                intent = new Intent(this, ListaPaesiActivity.class);
                bundle = new Bundle();
                bundle.putString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                        ListaPaesiActivity.class.getName());
                bundle.putInt(res.getString(R.string.ATTIVITÀ_LANCIATA),1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.buttonArgomento:
                intent = new Intent(this, ListaArgomentiActivity.class);
                bundle = new Bundle();
                bundle.putString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                        ListaArgomentiActivity.class.getName());
                bundle.putInt(res.getString(R.string.ATTIVITÀ_LANCIATA),1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.buttonGrafico:
                intent = new Intent(this, MostraImmagineActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonDatabase:
                intent = new Intent(this, CaricaDatiActivity.class);
                startActivity(intent);
        }
    }




}

