package com.example.world_bank_v4;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GraficoActivity extends AppCompatActivity implements View.OnClickListener{

    private final String Nome_App = "WorldBank: ";

    private Intent intent_prec;
    private Bundle bundle;
    String json_file;

    DbManager dbManager;


    private ArrayList<Grafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*in this example, a LineChart is initialized from xml*/
        chart = (LineChart) findViewById(R.id.chart);
        button = findViewById(R.id.button);

        button.setOnClickListener(this);

        /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        intent_prec = getIntent();
        if(intent_prec != null) {
            bundle = intent_prec.getExtras();
            json_file = bundle.getString("file_json_indicatore_per_paese");

            /*DEBUG*/
            Log.d(Nome_App + "JSON FILE ", json_file);
        }



        /*con la libreria GSON ottengo la corrispondente lista/array dei dati del grafico
         del file json*/
        MyGSON myGSON = new MyGSON();
        lista_grafico = myGSON.getListDatiGrafico(json_file);

        /*DEBUG*/
        Log.d(Nome_App + " DIM LISTA ",  String.valueOf(lista_grafico.size()));
        for(int i = 0; i<lista_grafico.size(); i++)
            Log.d(Nome_App, lista_grafico.get(i).toString() + "\n");

        Grafico graf;
        List<Entry> entries = new ArrayList<Entry>();
        for (int i=lista_grafico.size(); i>0;  i--) {
            graf = lista_grafico.get(i-1);
            if(graf.getvalue() == null){
                graf.resetValue();
            }
            entries.add(new Entry(Float.parseFloat(graf.getDate()), graf.getvalue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED); // styling, ...
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        /*DEBUG*/
        Log.d(Nome_App, "Disegnato Grafico");
    }


    @Override
    public void onClick(View v) {

        new DatabaseTask().execute(lista_grafico);
    }



    /*thread che in background salva i dati nel database locale*/
    private class DatabaseTask extends AsyncTask< ArrayList<Grafico>, Void, String > {

        @Override
        protected String doInBackground(ArrayList<Grafico> ... params) {

            ArrayList<Grafico> lista_grafico = params[0];
            dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            for(int i=lista_grafico.size(); i>0; i--){
                Grafico elemento = lista_grafico.get(i-1);
                dbManager.addRow(elemento.getDate(), elemento.getvalue().toString());
                Log.d(Nome_App, "aggiunta riga nel database");

            }

            String risultato = "Dati salvati nel database";
            return risultato;
        }



        protected void onPostExecute(String risultato){
            Log.d(Nome_App, risultato);

        }
    }

}
