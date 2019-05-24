package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.world_bank_v4.Adapters.ArgomentiAdapter;
import com.example.world_bank_v4.Model.Argomento;
import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class ListaArgomentiActivity extends ListaGenericaActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*"specializza activity*/
        setContentView(R.layout.activity_lista_argomenti);
        getSupportActionBar().setLogo(R.drawable.topic);

        ArrayList<Argomento> lista_argomenti = new ArrayList<Argomento>();
        TypeToken<ArrayList<Argomento>> listTypeToken = new TypeToken<ArrayList<Argomento>>() {};

        super.setIdListView( R.id.list_view);
        super.setProgressBar(R.id.progressBar);
        super.setLista_oggetti(lista_argomenti);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_ARGOMENTI);
        super.setNOME_FILE_PREFERENCES(Costanti.PREFERENCES_FILE_ARGOMENTI);
        super.setAPI_WORLD_BANK(Costanti.API_TOPIC_LIST_FORMAT_JSON);

        /*ottiene dal sito a dal disco i dati che occorrono a riempire la ListView, e li collega
        a quest'ultima*/

        super.caricaLista();
    }





    @Override
    public void instanziaAdapter(){
        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        super.setAdapter(new ArgomentiAdapter(this, R.layout.riga_layout,
                super.getListaOggetti()));;
    }


    @Override
    public String costruisciApi(){
        /*API:  "https://api.worldbank.org/v2/topic?format=json/"*/
        return (Costanti.API_TOPIC_LIST_FORMAT_JSON);
    }




    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
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

        Bundle bundle_succ = super.getBundleSucc();
        bundle_succ.putString(Costanti.ID_PAESE_SELEZIONATO, super.getIdPaeseSelezionato());
        bundle_succ.putString(Costanti.NOME_PAESE_SELEZIONATO, super.getNomePaeseSelezionato());
        ArrayList<Argomento> lista_argomenti = super.getListaOggetti();
        bundle_succ.putString(Costanti.ID_ARGOMENTO_SELEZIONATO,
                                        lista_argomenti.get(position++).getId());

        /*questa attività lancia sempre l'attività ListIndicatoriActivity*/
        Intent intent_succ = new Intent(getApplicationContext(), ListaIndicatoriActivity.class);
        intent_succ.putExtras(bundle_succ);
        startActivityForResult(intent_succ,Costanti.lista_argomenti_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

    }



}