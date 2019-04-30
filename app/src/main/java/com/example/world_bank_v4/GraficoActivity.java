package com.example.world_bank_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GraficoActivity extends ListaGenericaActivity implements View.OnClickListener{

    private String json_file;
    private DbManager dbManager;
    private ArrayList<Grafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;
    private Button button_salva_database;
    private Button button_salva_grafico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*"specializza activity*/
        setContentView(R.layout.activity_grafico);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*in this example, a LineChart is initialized from xml*/
        chart = findViewById(R.id.chart);
        button_salva_grafico = findViewById(R.id.button_salva_grafico);
        button_salva_grafico.setOnClickListener(this);
        button_salva_database = findViewById(R.id.button_salva_database);
        button_salva_database.setOnClickListener(this);

        ArrayList<Grafico> lista_grafico = new ArrayList<Grafico>();
        TypeToken<ArrayList<Grafico>> listTypeToken = new TypeToken<ArrayList<Grafico>>() {
        };
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE);
        super.setNOME_FILE_PREFERENCES(Costanti.PREFERENCES_FILE_INDICATORE_PER_PAESE);
        /*per costruire l'api devo aspettare che la classe ListaIndicatoriActivity mi passi
        l'intento con il Paese selezionato dall'utente*/
        super.setAPI_WORLD_BANK(null);

        /*ottiene dal sito a dal disco i dati che occorrono a riempire la ListView, e li collega
        a quest'ultima*/
        super.caricaLista();
    }
    @Override
    public String costruisciApi(){
        /*costruisci la stringa api per ottenere una lista di valori relativi
        all'indicatore per paese selezionato*/
        StringBuilder api_indicatore_per_paese = new StringBuilder();
        /*API_COUNTRY_LIST = "https://api.worldbank.org/v2/country/"*/
        api_indicatore_per_paese.append(Costanti.API_COUNTRY_LIST);
        api_indicatore_per_paese.append(super.getIdPaeseSelezionato());
        api_indicatore_per_paese.append("/indicator/");
        api_indicatore_per_paese.append(super.getIdIndicatoreSelezionato());
        /*API_INDICATORE_PER_PAESE
        https://api.worldbank.org/v2/country/idPaese/indicator?format=json&per_page=10000*/
        api_indicatore_per_paese.append("?format=json&&per_page=10000");
        return api_indicatore_per_paese.toString();
    }

    /*riceve il file json, lo trasforma con GSON in una List<T>, e collega quest'ultima al chart*/
    @Override
    public void caricaLayoutLista(){

        json_file = (super.getJsonFile());

        /*DEBUG*/
        Log.d(Costanti.NOME_APP + "JSON FILE ", json_file);

        /*con la libreria GSON ottengo la corrispondente lista/array di argomenti del file json*/
        MyGSON myGSON = new MyGSON();
        lista_grafico = myGSON.getList(json_file, new TypeToken<ArrayList<Grafico>>() {});

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return false;
    }


    /*a seconda del bottone che è stato cliccato, lancia il relativo thread task in bakground*/
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

            return "Dati salvati nel database";
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
            /*stream per scrivere il grafico bitmap sul disco*/
            FileOutputStream outputStream;
            try{
                /*apre 1 stream in scrittura verso 1 file nello storage interno, privato per l'app.
                se il file non esiste lo crea*/
                outputStream = openFileOutput(Costanti.NOME_UNICO_FILE_PNG, Context.MODE_PRIVATE);
                /*la qualità 80% vale solo se il formato è JPEG.
                Write a compressed version of the bitmap to the specified outputstream.
                If this returns true, the bitmap can be reconstructed by passing a
                corresponding inputstream to BitmapFactory.decodeStream(). Note: not all Formats
                support all bitmap configs directly, so it is possible that the returned bitmap
                from BitmapFactory could be in a different bitdepth, and/or may have lost per-pixel
                alpha (e.g. JPEG only supports opaque pixels).*/
                if(bitmap_chart.compress(Bitmap.CompressFormat.PNG, 80 , outputStream)){
                    Log.d(Costanti.NOME_APP, "chart_bitmap compresso in PNG su file");
                }
                else  Log.d(Costanti.NOME_APP, "Errore compressione bitmap in PNG su file");

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
