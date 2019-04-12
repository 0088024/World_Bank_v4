package com.example.world_bank_v4;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GraficoActivity extends AppCompatActivity {

    private final String Nome_App = "WorldBank: ";

    private Intent intent_prec;
    private Bundle bundle;


    private ArrayList<Grafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // in this example, a LineChart is initialized from xml
        chart = (LineChart) findViewById(R.id.chart);

        /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        intent_prec = getIntent();
        bundle = intent_prec.getExtras();
        String json = bundle.getString("file_json_indicatore_per_paese");

        /*DEBUG*/
        Log.d(Nome_App + "JSON FILE ", json);

        /*attraverso il parser di Gson ottengo l'elemento che mi interessa: l'array di json*/
        JsonElement je = new JsonParser().parse(json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_indicatori = je2.getAsJsonArray();   /*qui ho l'array json degli indicatori*/
        Log.d(Nome_App + " DIM[]", String.valueOf(array_indicatori.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Grafico, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Grafico>> listType = new TypeToken<ArrayList<Grafico>>() {};
        lista_grafico = gson.fromJson(je2, listType.getType());

        /*DEBUG*/
        Log.d(Nome_App + " DIM LISTA ",  String.valueOf(lista_grafico.size()));
        for(int i = 0; i<lista_grafico.size(); i++)
            Log.d(Nome_App, lista_grafico.get(i).toString() + "\n");

        Grafico graf;
        List<Entry> entries = new ArrayList<Entry>();
        for (int i=lista_grafico.size(); i>0;  i--) {
            graf = lista_grafico.get(i-1);
            // turn your data into Entry objects
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
}
