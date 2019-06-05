package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.world_bank_v4.Dialog.DialogAbout;
import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.Dialog.DialogContacts;
import com.example.world_bank_v4.Dialog.DialogDataMissing;
import com.example.world_bank_v4.Dialog.DialogImageMissing;
import com.example.world_bank_v4.R;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    private Intent intent;
    private ImageView imageView;
    private Button buttonPaese;
    private Button buttonArgomento;
    private Button buttonGrafico;
    private Button buttonDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setLogo(R.drawable.world_bank);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                DialogAbout mydialogabout = new DialogAbout();
                mydialogabout.show(getSupportFragmentManager(), "mydialog");
                break;

            case R.id.Menu_3:
                DialogContacts mydialog = new DialogContacts();
                mydialog.show(getSupportFragmentManager(), "mydialog");
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
                startActivityForResult(intent,
                        res.getInteger(R.integer.LISTA_PAESI_CODE));
                break;

            case R.id.buttonArgomento:
                intent = new Intent(this, ListaArgomentiActivity.class);
                bundle = new Bundle();
                bundle.putString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                        ListaArgomentiActivity.class.getName());
                bundle.putInt(res.getString(R.string.ATTIVITÀ_LANCIATA),1);
                intent.putExtras(bundle);
                startActivityForResult(intent,
                        res.getInteger(R.integer.LISTA_ARGOMENTI_CODE));
                break;

            case R.id.buttonGrafico:
                intent = new Intent(this, MostraGraficoActivity.class);
                startActivityForResult(intent,
                        res.getInteger(R.integer.MOSTRA_GRAFICO_CODE));
                break;

            case R.id.buttonDatabase:
                intent = new Intent(this, CaricaDatiActivity.class);
                startActivityForResult(intent,
                        res.getInteger(R.integer.MOSTRA_DATABASE_CODE));
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Resources res = getResources();
        if((requestCode == res.getInteger(R.integer.LISTA_PAESI_CODE) ||
                requestCode == res.getInteger(R.integer.LISTA_ARGOMENTI_CODE))
                            && resultCode == RESULT_FIRST_USER){

                String error_message = data.getStringExtra("error");
                intent = new Intent(this, NotificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("error", error_message);
                intent.putExtras(bundle);
                startActivity(intent);

        }

        if((requestCode == Costanti.MOSTRA_GRAFICO_CODE && resultCode == RESULT_FIRST_USER)){
            DialogImageMissing mydialog = new DialogImageMissing();
            mydialog.show(getSupportFragmentManager(), "mydialog");

        }

        if((requestCode == Costanti.MOSTRA_DATABASE_CODE && resultCode == RESULT_FIRST_USER)){
            DialogDataMissing mydialog = new DialogDataMissing();
            mydialog.show(getSupportFragmentManager(), "mydialog");

        }
    }
}

