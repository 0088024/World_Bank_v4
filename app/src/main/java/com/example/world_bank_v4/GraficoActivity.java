package com.example.world_bank_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.Chart;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GraficoActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent_prec;
    private Bundle bundle;
    private String json_file;
    private DbManager dbManager;
    private ArrayList<Grafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;
    private Button button_salva_database;
    private Button button_salva_grafico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*in this example, a LineChart is initialized from xml*/
        chart = findViewById(R.id.chart);
        button_salva_grafico = findViewById(R.id.button_salva_grafico);
        button_salva_grafico.setOnClickListener(this);
        button_salva_database = findViewById(R.id.button_salva_database);
        button_salva_database.setOnClickListener(this);

        /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        intent_prec = getIntent();
        if(intent_prec != null) {
            bundle = intent_prec.getExtras();
            if(bundle!=null){
                json_file = bundle.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE);
            }
            /*se l'intento non contiene la stringa passata dall'attività genitore, significa che
            l'attività è stata ripresa (per esempio l'utente torna da quella successiva) e non
            lanciata da quella precedente, quindi carico in memoria il file dalle preferenze
            condivise precedentemente salvate*/
            else {
                SharedPreferences sharedPreferences =
                        getSharedPreferences(Costanti.PREFERENCES_FILE_INDICATORE_PER_PAESE,
                                Context.MODE_PRIVATE);
                json_file =
                        sharedPreferences.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE,
                                "File non esiste");
            }
        }
        /*se l'oggetto savedInstanceState non è null signifa che il sistema ha ricreato un'attività
        precedentemente distrutta e quindi ti fornisce l'oggetto Bundle salvato*/
        else{
              json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE);
        }

        /*con la libreria GSON ottengo la corrispondente lista/array dei dati del grafico
         del file json*/
        MyGSON myGSON = new MyGSON();
        lista_grafico = myGSON.getListDatiGrafico(json_file);

        /*DEBUG*/
        Log.d(Costanti.NOME_APP + " DIM LISTA ",  String.valueOf(lista_grafico.size()));
        for(int i = 0; i<lista_grafico.size(); i++)
            Log.d(Costanti.NOME_APP, lista_grafico.get(i).toString() + "\n");

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
        Log.d(Costanti.NOME_APP, "Disegnato Grafico");
    }



    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
   prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
   dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE, json_file);

    }


    /*viene chiamato dal sistema quando l'attività è ripresa: dopo onStop()*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE,
                "File non esiste");
    }


    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua l'oggetto Bundle non viene
    salvato. O meglio, non mi viene passato in Oncreate(). Eppure la guida dice che se l'attività
    viene distrutta per vincoli di sistema dovrebbe ripristinarle e non crerae una nuova istanza.
    perchè?????????????????????????????????' */
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref =
                getSharedPreferences(Costanti.PREFERENCES_FILE_INDICATORE_PER_PAESE,
                                                            Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE, json_file);
        editor.apply();
    }



    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_salva_database:
                new SalvaDatabaseTask().execute(lista_grafico);
                break;
            case R.id.button_salva_grafico:
                new SalvaGraficoTask().execute(chart);
                break;
        }
    }



    /*thread che in background salva i dati nel database locale*/
    private class SalvaDatabaseTask extends AsyncTask< ArrayList<Grafico>, Void, String > {

        @Override
        protected String doInBackground(ArrayList<Grafico> ... params) {

            ArrayList<Grafico> lista_grafico = params[0];
            dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            for(int i=lista_grafico.size(); i>0; i--){
                Grafico elemento = lista_grafico.get(i-1);
                dbManager.addRow(elemento.getDate(), elemento.getvalue().toString());

                /*DEBUG*/
                Log.d(Costanti.NOME_APP, "aggiunta riga nel database");
            }

            String risultato = "Dati salvati nel database";
            return risultato;
        }



        protected void onPostExecute(String risultato){
            Log.d(Costanti.NOME_APP, risultato);

        }
    }



    /*thread che in background salva il grafico in un file png*/
    private class SalvaGraficoTask extends AsyncTask< Chart, Void, String > {

        @Override
        protected String doInBackground(Chart ... params) {

            Chart chart = params[0];
            /* Returns the Bitmap object that represents the chart, this Bitmap always contains the
            latest drawing state of the chart.*/
            Bitmap bitmap_chart = chart.getChartBitmap();
            FileOutputStream outputStream;
            try{
                outputStream = openFileOutput("myGrafico",Context.MODE_PRIVATE);
                /*la qualità 80% vale solo se il formato è JPEG*/
                /**
                 * Write a compressed version of the bitmap to the specified outputstream.
                 * If this returns true, the bitmap can be reconstructed by passing a
                 * corresponding inputstream to BitmapFactory.decodeStream(). Note: not
                 * all Formats support all bitmap configs directly, so it is possible that
                 * the returned bitmap from BitmapFactory could be in a different bitdepth,
                 * and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque
                 * pixels).*/
                bitmap_chart.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.close();

            } catch (FileNotFoundException e) {
                Log.d(Costanti.NOME_APP, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(Costanti.NOME_APP, e.getMessage());

                e.printStackTrace();
            }


            return "Grafico salvato in png";

        }


        protected void onPostExecute(String risultato){
            Log.d(Costanti.NOME_APP, risultato);

        }
    }

}
