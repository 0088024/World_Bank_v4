package com.example.world_bank_v4;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListaIndicatoriActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private final String Nome_App = "WorldBank: ";
    private final String api_paesi = "https://api.worldbank.org/v2/country/";


    private ListView listView;
    private ArrayList<Indicatore> lista_indicatori;      /*lista che conterrà gli oggetti Indicatore*/
    private IndicatoriAdapter indicatori_adapter;

    private URL url;
    private DownloadFileTask thread;
    private Intent intent_prec;
    private Intent intent_succ;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_indicatori);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

         /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        intent_prec = getIntent();
        bundle = intent_prec.getExtras();
        String json = bundle.getString("file_json_indicatori_per_argomento");
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
        /*costruisci api per scaricare l'indicatore selezionato per il relativo paese*/
        try {
            StringBuilder api_indicatore_per_paese = new StringBuilder();
            api_indicatore_per_paese.append(api_paesi);
            String codicePaese = bundle.getString("idPaese");

            /*DEBUG*/
            Log.d(Nome_App + "codicePaese ", codicePaese);

            api_indicatore_per_paese.append(codicePaese);
            api_indicatore_per_paese.append("/indicator/");
            position++;
            api_indicatore_per_paese.append(lista_indicatori.get(position).getId());
            bundle.putString("idIndicatore", lista_indicatori.get(position).getId());
            api_indicatore_per_paese.append("?format=json&&per_page=10000");

            /*DEBUG*/
            Log.d(Nome_App + "api", api_indicatore_per_paese.toString());

            url = new URL(api_indicatore_per_paese.toString());
        }
        /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
        catch (MalformedURLException e) {
            Log.d(Nome_App, e.getMessage());
        }

        new DownloadFileTask().execute(url);

    }


    /*thread che in background scarica in una stringa il file json dell'indicatore per paese*/
    private class DownloadFileTask extends AsyncTask<URL, Void, String> {

        private InputStream risposta;
        private StringBuilder sb;
        private HttpURLConnection client;

        @Override
        protected String doInBackground(URL... urls) {

            try {
                /*creo l'oggetto HttpURLConnection e apro la connessione al server*/
                client = (HttpURLConnection) urls[0].openConnection();

                /*Recupero le informazioni inviate dal server*/
                risposta = new BufferedInputStream(client.getInputStream());

                /*leggo i caratteri e li appendo in sb*/
                sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(risposta));
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }

            } catch (IOException e) {
                Log.d(Nome_App, e.getMessage());

            } finally {
                client.disconnect();
            }

            /*convert StringBuilder to String using toString() method*/
            String json = sb.toString();

            return json;
        }

        protected void onPostExecute(String risultato) {
            int requestCode = 1;

            /*DEBUG*/
            Log.d(Nome_App, risultato);

            intent_succ = new Intent(getApplicationContext(),GraficoActivity.class);
            bundle.putString("file_json_indicatore_per_paese", risultato);
            intent_succ.putExtras(bundle);
            startActivityForResult(intent_succ,requestCode);
        }

    }

}
