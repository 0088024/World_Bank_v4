package com.example.world_bank_v4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ListaIndicatoriActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private final String Nome_App = "WorldBank: ";

    private ListView listView;
    private ArrayList<Indicatore> lista_indicatori;      /*lista che conterrà gli oggetti Indicatore*/
    IndicatoriAdapter indicatori_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_indicatori);

         /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        Intent intent = getIntent();
        String json = intent.getStringExtra("chiave");
        Log.d(Nome_App + "JSON FILE ", json);

        /*attraverso il parser di Gson ottengo l'elemento che mi interessa: l'array di json*/
        JsonElement je = new JsonParser().parse(json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);
        /*DEBUG*/
        JsonArray array_indicatori = je2.getAsJsonArray();   /*qui ho l'array json degli indicatori*/
        Log.d(Nome_App + " DIM INDIC[]", String.valueOf(array_indicatori.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Indicatore>> listType = new TypeToken<ArrayList<Indicatore>>() {};
        lista_indicatori = gson.fromJson(je2, listType.getType());
        /*DEBUG*/
        Log.d(Nome_App + " DIM LISTA ",  String.valueOf(lista_indicatori.size()));
        for(int i = 0; i<lista_indicatori.size(); i++)
            Log.d(Nome_App, lista_indicatori.get(i).toString() + "\n");

        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        indicatori_adapter = new IndicatoriAdapter(this, R.layout.riga_layout, lista_indicatori);
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(indicatori_adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(view.getContext(), "CLICK ON " + position + ":" +
                lista_indicatori.get(position).toString(), Toast.LENGTH_LONG).show();
    }
}
