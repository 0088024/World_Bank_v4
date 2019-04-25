package com.example.world_bank_v4;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;


public class ListaPaesiActivity extends ListaGenericaActivity
        implements AdapterView.OnItemClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_paese_activity);
        getSupportActionBar().setLogo(R.drawable.country);

        ArrayList<Paese> lista_paesi = new ArrayList<Paese>();
        TypeToken<ArrayList<Paese>> listTypeToken = new TypeToken<ArrayList<Paese>>() {
        };

        super.specializzaActivity(R.id.list_view_paesi, lista_paesi, listTypeToken,
                Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500, Costanti.KEY_JSON_FILE_COUNTRY,
                Costanti.PREFERENCES_FILE_PAESI);

    }

    @Override
    public void instanziaAdapter(){
        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        super.setAdapter(new PaesiAdapter(this, R.layout.riga_layout,
                super.getListaOggetti()));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        return false;
    }


    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        /*savedInstanceState.putString(Costanti.KEY_JSON_FILE_COUNTRY, json_file);
        savedInstanceState.putString(Costanti.NOME_CLASSE_SELEZIONATA, nome_classe_selezionata);*/
        /*savedInstanceState.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);*/

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
