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
import java.util.List;

public class ListaPaesiActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private URL url;
    private ListView listView;
    private ArrayList<Paese> lista_paesi;               /*lista che conterrà gli oggetti Paese*/
    private PaesiAdapter paesi_adapter;
    private Intent intent_prec;
    private Intent intent_succ;
    private Bundle bundle;
    private String json_file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_paese_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.country);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*se è null significa che l'attività è stata creata nuova*/
        if(savedInstanceState == null){
            /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
            il file json scaricato da WorldBank*/
            intent_prec = getIntent();         /*ritorna l'intento ch ha startato questa activity*/
            if(intent_prec!=null){
                json_file = intent_prec.getStringExtra(Costanti.KEY_JSON_FILE_COUNTRY);
                /*se l'intento non contiene la stringa passata dall'attività genitore, significa che
                l'attività è stata ripresa (per esempio l'utente torna da quella successiva) e non
                lanciata da quella precedente, quindi carico in memoria il file dalle preferenze
                condivise precedentemente salvate*/
                if(json_file == null){
                    SharedPreferences sharedPreferences =
                            getSharedPreferences(Costanti.PREFERENCES_FILE_PAESI,
                                                                        Context.MODE_PRIVATE);
                    json_file = sharedPreferences.getString(Costanti.KEY_JSON_FILE_COUNTRY,
                                                                "File non esiste");
                }
            }
        }
        /*se l'oggetto savedInstanceState non è null signifa che il sistema ha ricreato un'attività
        precedentemente distrutta e quindi ti fornisce l'oggetto Bundle salvato*/
        else{
            json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_COUNTRY);
        }

        /*DEBUG*/
        Log.d(Costanti.NOME_APP + "JSON FILE ", json_file);

        /*con la libreria GSON ottengo la corrispondente lista/array di paesi del file json*/
        MyGSON myGSON = new MyGSON();
        lista_paesi = myGSON.getListPaesi(json_file);

        /*DEBUG*/
        Log.d(Costanti.NOME_APP + " DIM LISTA ",  String.valueOf(lista_paesi.size()));
        for(int i = 0; i<lista_paesi.size(); i++)
            Log.d(Costanti.NOME_APP, lista_paesi.get(i).toString() + "\n");

        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        paesi_adapter = new PaesiAdapter(this, R.layout.riga_layout, lista_paesi);
        listView = findViewById(R.id.list_view_paesi);
        listView.setAdapter(paesi_adapter);

        listView.setOnItemClickListener(this);
    }


    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Costanti.KEY_JSON_FILE_COUNTRY, json_file);
    }


    /*viene chiamato dal sistema quando l'attività è ripresa: dopo onStop()*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_COUNTRY,
                                                        "File non esiste");

    }


    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua l'oggetto Bundle non viene
    salvato. O meglio, non mi viene passato in Oncreate(). Eppure la guida dice che se l'attività
    viene distrutta per vincoli di sistema dovrebbe ripristinarle e non crerae una nuova istanza.
    perchè?????????????????????????????????'*/
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref =
                getSharedPreferences(Costanti.PREFERENCES_FILE_PAESI, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Costanti.KEY_JSON_FILE_COUNTRY, json_file);
        editor.apply();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*indifferentemente dal paese selezionato scarica la l'unica lista degli
            argomenti, ma aggiungi il codice del paese al bundle nel'intento da passare*/
            try {
                url = new URL(Costanti.API_TOPIC_LIST);
            }
            /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
            catch (MalformedURLException e) {
                Log.d(Costanti.NOME_APP, e.getMessage());
            }

            new DownloadFileTask().execute(url);

            StringBuilder sb = new StringBuilder();
            sb.append(Costanti.API_COUNTRY_LIST);
            sb.append(lista_paesi.get(position).getId() + "/");
            bundle = new Bundle();
            bundle.putString("idPaeseSelezionato", lista_paesi.get(position++).getId());
    }


    /*thread che in background scarica in una stringa il file json degli argomenti*/
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
                Log.d(Costanti.NOME_APP, e.getMessage());

            } finally {
                client.disconnect();
            }

            /*convert StringBuilder to String using toString() method*/
            return sb.toString();
        }

        protected void onPostExecute(String risultato) {
            int requestCode = 1;
            intent_succ = new Intent(getApplicationContext(), ListaArgomentiActivity.class);
            bundle.putString(Costanti.KEY_JSON_FILE_ARGOMENTI, risultato );
            intent_succ.putExtras(bundle);
            startActivityForResult(intent_succ,requestCode);
        }
    }


    @Override
    protected void onActivityResult (int requestCodeID, int resultCode, Intent intent){

    }
}
