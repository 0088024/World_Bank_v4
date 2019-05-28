package com.example.world_bank_v4.Activities;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;


import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.Adapters.MyGenericoAdapter;
import com.example.world_bank_v4.Model.Paese;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class ListaPaesiActivity extends ListaGenericaActivity {

    private boolean mReturningWithResult = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*"specializza activity*/
        setContentView(R.layout.activity_lista_paese);
        getSupportActionBar().setLogo(R.drawable.country);
    }




    @Override
    public void onResume(){
        ArrayList<Paese> lista_paesi = new ArrayList<Paese>();
        TypeToken<ArrayList<Paese>> listTypeToken = new TypeToken<ArrayList<Paese>>() {};
        super.setIdListView(R.id.list_view_paesi);
        super.setProgressBar(R.id.progressBar);
        super.setLista_oggetti(lista_paesi);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_COUNTRY);
        super.setNOME_FILE_PREFERENCES(Costanti.PREFERENCES_FILE_PAESI);
        super.setAPI_WORLD_BANK(Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500);
        super.onResume();
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





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        super.onItemClick(parent, view, position, id);
        ArrayList<Paese> lista_paesi = super.getListaOggetti();
        int posizione = position++;
        Bundle bundle_succ = super.getBundleSucc();
        bundle_succ.putString(Costanti.ID_PAESE_SELEZIONATO, lista_paesi.get(posizione).getId());
        bundle_succ.putString(Costanti.NOME_PAESE_SELEZIONATO,
                                                    lista_paesi.get(posizione).getName());
        bundle_succ.putString(Costanti.ID_INDICATORE_SELEZIONATO,
                                                    super.getIdIndicatoreSelezionato());
        bundle_succ.putString(Costanti.NOME_INDICATORE_SELEZIONATO,
                                                    super.getNomeIndicatoreSelezionato());

        Class<?> classe = ListaPaesiActivity.class;
        Intent intent_succ;
        if(super.getNomeClasseSelezionata().contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), ListaArgomentiActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);

        intent_succ.putExtras(bundle_succ);
        startActivityForResult(intent_succ,0);
    }


}
