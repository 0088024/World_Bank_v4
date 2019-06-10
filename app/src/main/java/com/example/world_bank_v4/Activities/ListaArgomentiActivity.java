package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.world_bank_v4.Adapters.ArgomentiAdapter;
import com.example.world_bank_v4.Model.Argomento;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class ListaArgomentiActivity extends ListaGenericaActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*"specializza activity*/
        setContentView(R.layout.activity_lista_argomenti);
    }



    @Override
    public void onResume(){
        Resources res = getResources();
        getSupportActionBar().setLogo(R.drawable.topic);
        ArrayList<Argomento> lista_argomenti = new ArrayList<Argomento>();
        TypeToken<ArrayList<Argomento>> listTypeToken = new TypeToken<ArrayList<Argomento>>() {};
        super.setIdListView(R.id.list_view);
        super.setProgressBar(R.id.progressBar);
        super.setLista_oggetti(lista_argomenti);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(res.getString(R.string.KEY_JSON_FILE_ARGOMENTI));
        super.setNOME_FILE_PREFERENCES(res.getString(R.string.PREFERENCES_FILE_ARGOMENTI));
        super.setAPI_WORLD_BANK(res.getString(R.string.API_TOPIC_LIST_FORMAT_JSON));
        super.onResume();

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
        return (getResources().getString(R.string.API_TOPIC_LIST_FORMAT_JSON));
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
        ArrayList<Argomento> lista_argomenti = super.getListaOggetti();
        bundle_succ.putString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO),
                                        lista_argomenti.get(posizione).getId());
        bundle_succ.putInt(res.getString(R.string.ATTIVITÀ_LANCIATA),1);


        /*questa attività lancia sempre l'attività ListIndicatoriActivity*/
        Intent intent_succ = new Intent(getApplicationContext(), ListaIndicatoriActivity.class);
        intent_succ.putExtras(bundle_succ);
        Log.d(res.getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": CODE = " +
                        String.valueOf(res.getInteger(R.integer.LISTA_ARGOMENTI_CODE)) );
        startActivityForResult(intent_succ,
                res.getInteger(R.integer.LISTA_ARGOMENTI_CODE));
    }

}
