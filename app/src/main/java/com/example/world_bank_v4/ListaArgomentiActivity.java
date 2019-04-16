package com.example.world_bank_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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

public class ListaArgomentiActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener{

    private final String Nome_App = "WorldBank: ";
    private final String api_topic = "https://api.worldbank.org/v2/topic/";


    private URL url;
    private String json_file;
    private ListView listView;
    private ArrayList<Argomento> lista_argomenti;       /*lista che conterrà gli oggetti Argomento*/
    private ArgomentiAdapter argomenti_adapter;
    private Intent intent_prec;
    private Intent intent_succ;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_argomenti);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.topic);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*se è null significa che l'attività è stata creata nuova*/
        if(savedInstanceState == null){
            /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
            il file json scaricato da WorldBank*/
            intent_prec = getIntent();
            if(intent_prec!=null){
                bundle = intent_prec.getExtras();
                if(bundle!=null){
                    json_file = bundle.getString("json_file_argomenti");
                    /*se l'intento non contiene la stringa passata dall'attività genitore, significa che
                    l'attività è stata ripresa (per esempio l'utente torna da quella successiva) e non
                    lanciata da quella precedente, quindi carico in memoria il file dalle preferenze
                    condivise precedentemente salvate*/
                    if(json_file == null){
                        SharedPreferences sharedPreferences =
                                getSharedPreferences("Preferences_Argomenti", Context.MODE_PRIVATE);
                        json_file = sharedPreferences.getString("json_file_argomenti",
                                "File non esiste");
                    }
                }

            }
        }
        /*se l'oggetto savedInstanceState non è null signifa che il sistema ha ricreato un'attività
        precedentemente distrutta e quindi ti fornisce l'oggetto Bundle salvato*/
        else{
            json_file = savedInstanceState.getString("json_file_argomenti");
        }

        /*con la libreria GSON ottengo la corrispondente lista/array di argomenti del file json*/
        MyGSON myGSON = new MyGSON();
        lista_argomenti = myGSON.getListArgomenti(json_file);

        /*DEBUG*/
        Log.d(Nome_App + " DIM LISTA ",  String.valueOf(lista_argomenti.size()));
        for(int i = 0; i<lista_argomenti.size(); i++)
            Log.d(Nome_App, lista_argomenti.get(i).toString() + "\n");

        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        argomenti_adapter = new ArgomentiAdapter(this, R.layout.riga_layout, lista_argomenti);
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(argomenti_adapter);

        listView.setOnItemClickListener(this);
    }



    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("json_file_argomenti", json_file);
    }


    /*viene chiamato dal sistema quando l'attività è ripresa: dopo onStop()*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        json_file = savedInstanceState.getString("json_file_argomenti",
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
                getSharedPreferences("Preferences_Argomenti", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("json_file_argomenti", json_file);
        editor.apply();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(view.getContext(), "CLICK ON " + position + ":" +
                    lista_argomenti.get(position).getValue(), Toast.LENGTH_LONG).show();
        /*costruisci la stringa api per ottenere una lista di indicatori relativi all'argomento
        selezionato*/
        try {
            StringBuilder api_indicatori_list_for_topic = new StringBuilder();
            api_indicatori_list_for_topic.append(api_topic);
            position++;
            api_indicatori_list_for_topic.append(position);
            api_indicatori_list_for_topic.append("/indicator?format=json&per_page=10000");
            url = new URL(api_indicatori_list_for_topic.toString());
        }
        /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
        catch (MalformedURLException e) {
            Log.d(Nome_App, e.getMessage());
        }

        new DownloadFileTask().execute(url);

    }


    @Override
    protected void onActivityResult(int requestCodeID, int resultCode, Intent intent){

    }


    /*thread che in background scarica in una stringa la lista degli indicatori relativi
     all'argomento selezionato*/
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
            intent_succ = new Intent(getApplicationContext(),ListaIndicatoriActivity.class);
            bundle.putString("json_file_indicatori_per_argomento", risultato);
            intent_succ.putExtras(bundle);
            startActivityForResult(intent_succ,requestCode);
        }

    }
}
