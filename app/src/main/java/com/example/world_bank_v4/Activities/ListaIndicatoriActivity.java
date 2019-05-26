package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.Model.Indicatore;
import com.example.world_bank_v4.Adapters.MyGenericoAdapter;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ListaIndicatoriActivity extends ListaGenericaActivity {

    private boolean mReturningWithResult = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*specializzaActivity*/
        setContentView(R.layout.activity_lista_indicatori);
        getSupportActionBar().setLogo(R.drawable.indicator);

        ArrayList<Indicatore> lista_indicatori = new ArrayList<Indicatore>();
        TypeToken<ArrayList<Indicatore>> listTypeToken = new TypeToken<ArrayList<Indicatore>>() {};

        super.setIdListView(R.id.list_view);
        super.setProgressBar(R.id.progressBar);
        super.setLista_oggetti(lista_indicatori);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_INDICATORI_PER_ARGOMENTO);
        super.setNOME_FILE_PREFERENCES(Costanti.PREFERENCES_FILE_INDICATORI_PER_ARGOMENTO);
        /*per costruire l'api devo aspettare che la classe ListaargomentiActivity mi passi
        l'intento con l'argomento selezionato dall'utente*/
        super.setAPI_WORLD_BANK(null);

        /*ottiene dal sito a dal disco i dati che occorrono a riempire la ListView, e li collega
        a quest'ultima*/
        /*super.caricaVariabili();*/

    }


    @Override
    public void onResume(){
        super.onResume();
    }




    @Override
    public String costruisciApi(){
        /*costruisci la stringa api per ottenere una lista di indicatori relativi all'argomento
        selezionato*/
        StringBuilder api_indicatori_per_argomento = new StringBuilder();
        /*API_TOPIC_LIST = "https://api.worldbank.org/v2/topic/"*/
        api_indicatori_per_argomento.append(Costanti.API_TOPIC_LIST);
        api_indicatori_per_argomento.append(super.getIdArgomentoSelezionato());
        /*API_INDICATORE_PER_ARGOMENTO
        https://api.worldbank.org/v2/topic/idArgomento/indicator?format=json&per_page=10000*/
        api_indicatori_per_argomento.append("/indicator?format=json&per_page=10000");
        return api_indicatori_per_argomento.toString();
    }



    @Override
    public void instanziaAdapter(){
        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        super.setAdapter(new MyGenericoAdapter(this, R.layout.riga_layout_indicators,
                                        super.getListaOggetti()));
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
        int posizione = position++;
        Bundle bundle_succ = super.getBundleSucc();
        bundle_succ.putString(Costanti.ID_PAESE_SELEZIONATO, super.getIdPaeseSelezionato());
        bundle_succ.putString(Costanti.NOME_PAESE_SELEZIONATO, super.getNomePaeseSelezionato());
        bundle_succ.putString(Costanti.ID_ARGOMENTO_SELEZIONATO, super.getIdArgomentoSelezionato());
        ArrayList<Indicatore> lista_indicatori = super.getListaOggetti();
        bundle_succ.putString(Costanti.ID_INDICATORE_SELEZIONATO,
                                                    lista_indicatori.get(posizione).getId());
        bundle_succ.putString(Costanti.NOME_INDICATORE_SELEZIONATO,
                lista_indicatori.get(posizione).getName());

        Class<?> classe = ListaPaesiActivity.class;
        Intent intent_succ;
        if(super.getNomeClasseSelezionata().contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), ListaPaesiActivity.class);

        intent_succ.putExtras(bundle_succ);
        startActivityForResult(intent_succ,Costanti.lista_indicatori_code);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
