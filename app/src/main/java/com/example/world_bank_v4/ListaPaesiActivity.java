package com.example.world_bank_v4;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;


public class ListaPaesiActivity extends ListaGenericaActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*"specializza activity*/

        setContentView(R.layout.activity_lista_paese_activity);
        getSupportActionBar().setLogo(R.drawable.country);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ArrayList<Paese> lista_paesi = new ArrayList<Paese>();
        TypeToken<ArrayList<Paese>> listTypeToken = new TypeToken<ArrayList<Paese>>() {};
        super.setIdListView(R.id.list_view_paesi);
        super.setLista_oggetti(lista_paesi);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_COUNTRY);
        super.setNOME_FILE_PREFERENCES(Costanti.PREFERENCES_FILE_PAESI);
        super.setAPI_WORLD_BANK(Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500);

        /*ottiene dal sito a dal disco i dati che occorrono a riempire la ListView, e li collega
        a quest'ultima*/

        super.caricaLista();




    }



    @Override
    public void instanziaAdapter(){
        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        super.setAdapter(new MyGenericoAdapter(this, R.layout.riga_layout,
                                                super.getListaOggetti()));
    }



    @Override
    public String costruisciApi(){
        /*API: https://api.worldbank.org/v2/country?format=json&per_page=500*/
        return Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500;
    }

    /*serve x salvare in un oggetto Bundle di sistema i dati dello stato dell'istanza.
    E' chiamato dal sistema prima di far entrare l'attività in onPause(). Se però l'attività è
    chiusa esplicitamente dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }



    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua, l'oggetto Bundle salvato
    in onSaveInstanceState() non viene salvato. O meglio, non mi viene passato in Oncreate().
    La guida dice che se l'attività viene distrutta per vincoli di sistema, il s.o. dovrebbe, ma
    non è sicuro, ripristinare (e quindi passando il Bundle) e non crerae una nuova istanza.*/
    @Override
    public void onPause(){
        super.onPause();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        super.onItemClick(parent, view, position, id);
        ArrayList<Paese> lista_paesi = super.getListaOggetti();
        Bundle bundle_succ = super.getBundleSucc();
        bundle_succ.putString(Costanti.ID_PAESE_SELEZIONATO, lista_paesi.get(position++).getId());
        bundle_succ.putString(Costanti.ID_INDICATORE_SELEZIONATO, super.getIdIndicatoreSelezionato());

        Class<?> classe = ListaPaesiActivity.class;
        Intent intent_succ;
        if(super.getNomeClasseSelezionata().contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), ListaArgomentiActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);

        intent_succ.putExtras(bundle_succ);
        startActivityForResult(intent_succ,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Intent intent;

        Log.d(Costanti.NOME_APP, "onActivityResultPaesi");

        // Controllo dei codici di risposta delle attività lanciate

        if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            // Errore imprevisto ad es. viene a mancare la connessione a internet
            String error_message = data.getStringExtra("error");
            Log.d(Costanti.NOME_APP + "ListPaeActiv", error_message);
            intent = new Intent(this, NotificationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("error", error_message);
            intent.putExtras(bundle);
            startActivity(intent);

        }
        if(requestCode == 0 && resultCode == RESULT_FIRST_USER){
            // Errore previsto ad es. nessun dato disponibile per un certo paese
            DialogNoCountry mydialog = new DialogNoCountry();
            mydialog.show(getSupportFragmentManager(),"mydialog");
        }
    }

}
