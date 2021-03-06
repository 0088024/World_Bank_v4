package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.world_bank_v4.Model.Indicatore;
import com.example.world_bank_v4.Adapters.MyGenericoAdapter;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ListaIndicatoriActivity extends ListaGenericaActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*specializzaActivity*/
        setContentView(R.layout.activity_lista_indicatori);
    }


    @Override
    public void onResume(){
        Resources res = getResources();
        getSupportActionBar().setLogo(R.drawable.indicator);
        ArrayList<Indicatore> lista_indicatori = new ArrayList<Indicatore>();
        TypeToken<ArrayList<Indicatore>> listTypeToken = new TypeToken<ArrayList<Indicatore>>() {};

        super.setIdListView(R.id.list_view);
        super.setProgressBar(R.id.progressBar);
        super.setLista_oggetti(lista_indicatori);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(res.getString(R.string.KEY_JSON_FILE_INDICATORI_PER_ARGOMENTO));
        super.setNOME_FILE_PREFERENCES(res.getString(R.string.PREFERENCES_FILE_INDICATORI_PER_ARGOMENTO));
        /*per costruire l'api devo aspettare che la classe ListaArgomentiActivity mi passi
        l'intento con l'argomento selezionato dall'utente*/
        super.setAPI_WORLD_BANK(null);
        super.onResume();

    }





    @Override
    public String costruisciApi(){
        /*costruisci la stringa api per ottenere una lista di indicatori relativi all'argomento
        selezionato*/
        StringBuilder api_indicatori_per_argomento = new StringBuilder();
        /*API_TOPIC_LIST = "https://api.worldbank.org/v2/topic/"*/
        api_indicatori_per_argomento.append(getResources().getString(R.string.API_TOPIC_LIST));
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



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Resources res = getResources();
        super.onItemClick(parent, view, position, id);
        int posizione = position++;
        Bundle bundle_succ = super.getBundleSucc();
        bundle_succ.putString(res.getString(R.string.ID_PAESE_SELEZIONATO),
                super.getIdPaeseSelezionato());
        bundle_succ.putString(res.getString(R.string.NOME_PAESE_SELEZIONATO),
                super.getNomePaeseSelezionato());
        bundle_succ.putString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO),
                super.getIdArgomentoSelezionato());
        ArrayList<Indicatore> lista_indicatori = super.getListaOggetti();
        bundle_succ.putString(res.getString(R.string.ID_INDICATORE_SELEZIONATO),
                                                    lista_indicatori.get(posizione).getId());
        bundle_succ.putString(res.getString(R.string.NOME_INDICATORE_SELEZIONATO),
                lista_indicatori.get(posizione).getName());
        bundle_succ.putInt(res.getString(R.string.ATTIVITÀ_LANCIATA) ,1);


        Class<?> classe = ListaPaesiActivity.class;
        Intent intent_succ;
        if(super.getNomeClasseSelezionata().contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), ListaPaesiActivity.class);

        intent_succ.putExtras(bundle_succ);
        Log.d(res.getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": CODE = " +
                        String.valueOf(res.getInteger(R.integer.LISTA_INDICATORI_CODE)) );
        startActivityForResult(intent_succ,
                res.getInteger(R.integer.LISTA_INDICATORI_CODE));
    }



}
