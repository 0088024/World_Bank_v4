package com.example.world_bank_v4;

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
import java.util.List;

public class ListaPaesiActivity extends AppCompatActivity {

    private final String Nome_App = "WorldBank: ";
    private final String api_topic_list = "http://api.worldbank.org/v2/topic?format=json";

    private URL url;
    private DownloadFileTask thread;

    private ListView listView;
    private ArrayList<Paese> lista_paesi;               /*lista che conterrà gli oggetti Paese*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_paese_activity);


         /*ottengo l'intent ricevuto dall'attività genitore e ne estrapolo la stringa contenente
        il file json scaricato da WorldBank*/
        Intent intent = getIntent();
        String json = intent.getStringExtra("chiave");

        /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di paesi
        json*/
        JsonElement je = new JsonParser().parse(json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);
        /*DEBUG*/
        JsonArray array_paesi = je2.getAsJsonArray();   /*qui ho l'array json dei paesi*/
        Log.d(Nome_App + " DIM PAESI[]", String.valueOf(array_paesi.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Paese>> listType = new TypeToken<ArrayList<Paese>>() {};
        lista_paesi = gson.fromJson(je2, listType.getType());
        /*DEBUG*/
        Log.d(Nome_App + " DIM LISTA ",  String.valueOf(lista_paesi.size()));
        for(int i = 0; i<lista_paesi.size(); i++)
            Log.d(Nome_App, lista_paesi.get(i).toString() + "\n");

        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        PaesiAdapter paesi_adapter = new PaesiAdapter(this, R.layout.riga_layout,
                                                        lista_paesi);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(paesi_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "CLICK ON " + position + ":" +
                                lista_paesi.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });
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
                Log.d(Nome_App, e.getMessage());

            } finally {
                client.disconnect();
            }

            /*convert StringBuilder to String using toString() method*/
            String json = sb.toString();

            return json;
        }



    }
}
