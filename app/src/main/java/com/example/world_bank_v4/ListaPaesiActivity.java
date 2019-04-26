package com.example.world_bank_v4;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;


public class ListaPaesiActivity extends ListaGenericaActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*"specializza activity*/
        setContentView(R.layout.activity_lista_paese_activity);
        getSupportActionBar().setLogo(R.drawable.country);
        ArrayList<Paese> lista_paesi = new ArrayList<Paese>();
        TypeToken<ArrayList<Paese>> listTypeToken = new TypeToken<ArrayList<Paese>>() {};
        super.setIdListView(R.id.list_view_paesi);
        super.setLista_oggetti(lista_paesi);
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_COUNTRY);
        super.setNOME_FILE_PREFERNCES(Costanti.PREFERENCES_FILE_PAESI);
        super.setAPI_WORLD_BANK(Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500);

        /*ottiene dal sito a dal disco i dati che occorrono a riempire la ListView, e li collega
        a quest'ultima*/
        super.caricaLista();
    }



    @Override
    public void instanziaAdapter(){
        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        super.setAdapter(new PaesiAdapter(this, R.layout.riga_layout,
                super.getListaOggetti()));
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

        Bundle bundle = new Bundle();
        ArrayList<Paese> lista_paesi = super.getListaOggetti();
        bundle.putString(Costanti.ID_PAESE_SELEZIONATO, lista_paesi.get(position++).getId());
        bundle.putString(Costanti.NOME_CLASSE_SELEZIONATA, super.getNomeClasseSelezionata());
        bundle.putString(Costanti.ID_INDICATORE_SELEZIONATO, super.getIdIndicatoreSelezionato());

        Class<?> classe = ListaPaesiActivity.class;
        Intent intent_succ;
        if(super.getNomeClasseSelezionata().contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), ListaArgomentiActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);

        intent_succ.putExtras(bundle);
        startActivity(intent_succ);
    }

}
