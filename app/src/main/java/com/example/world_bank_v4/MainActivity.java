package com.example.world_bank_v4;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;



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

        Intent intent;
        Bundle bundle;
        int id = item.getItemId();
        switch(id) {
            case R.id.Menu_1:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.worldbank.org"));
                startActivity(intent);
                break;

            case R.id.Menu_2:
                intent = new Intent(this, ListaArgomentiActivity.class);
                bundle = new Bundle();
                bundle.putString(Costanti.NOME_CLASSE_SELEZIONATA,
                                        ListaArgomentiActivity.class.getName());
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
                break;

            case R.id.Menu_3:
                intent = new Intent(this, MostraPngSalvatoPrecedentemente.class);
                startActivityForResult(intent,3);
                break;

            case R.id.Menu_4:
                intent = new Intent(this, CaricaDati.class);
                startActivityForResult(intent,4);
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        int id = v.getId();         /*ottengo l'id del bottone selezionato*/
        switch(id) {
            case R.id.buttonPaese:

                intent = new Intent(this, ListaPaesiActivity.class);
                bundle = new Bundle();
                bundle.putString(Costanti.NOME_CLASSE_SELEZIONATA,
                        ListaPaesiActivity.class.getName());
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
                break;

            case R.id.buttonArgomento:
                intent = new Intent(this, ListaArgomentiActivity.class);
                bundle = new Bundle();
                bundle.putString(Costanti.NOME_CLASSE_SELEZIONATA,
                        ListaArgomentiActivity.class.getName());
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
                break;

            case R.id.buttonGrafico:
                intent = new Intent(this, MostraPngSalvatoPrecedentemente.class);
                startActivityForResult(intent,3);
                break;

            case R.id.buttonDatabase:
                intent = new Intent(this, CaricaDati.class);
                startActivityForResult(intent,4);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if((requestCode == 1 || requestCode == 2) && resultCode == RESULT_CANCELED){

            String error_message = data.getStringExtra("error");
            intent = new Intent(this, NotificationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("error", error_message);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        if((requestCode == 3 && resultCode == RESULT_CANCELED)){
            DialogImageMissing mydialog = new DialogImageMissing();
            mydialog.show(getSupportFragmentManager(), "mydialog");

        }

        if((requestCode == 4 && resultCode == RESULT_CANCELED)){
            DialogDataMissing mydialog = new DialogDataMissing();
            mydialog.show(getSupportFragmentManager(), "mydialog");

        }
    }
}

