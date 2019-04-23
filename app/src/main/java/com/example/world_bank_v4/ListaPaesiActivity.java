package com.example.world_bank_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class ListaPaesiActivity extends AppCompatActivity
                                                implements AdapterView.OnItemClickListener {

    private URL url;
    private ListView listView;
    private ArrayList<Paese> lista_paesi;               /*lista che conterrà gli oggetti Paese*/
    private PaesiAdapter paesi_adapter;
    private Intent intent_prec;
    private Intent intent_succ;
    private Bundle bundle;
    private String json_file;
    private String nome_classe_selezionata;
    private String idIndicatoreSelezionato;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_paese_activity);
       /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.country);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        listView = findViewById(R.id.list_view_paesi);
        listView.setOnItemClickListener(this);

        for(;;) {
            /*se non è null significa che l'attività (non è stata lanciata da 1 altra attività, ma)
            è stata ripresa (per esempio l'utente torna da quella successiva) e reistanziata causa
            vincoli di integrità, e inoltre il s.o. ha passato l'oggetto bundle salvato in
            precedenza in onSaveInstancestate()*/
            if (savedInstanceState != null) {
                json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_COUNTRY,
                        "File non esiste");
                nome_classe_selezionata =
                        savedInstanceState.getString(Costanti.NOME_CLASSE_SELEZIONATA,
                        "File non esiste");
                /*può tornare null se l'attività è stata lanciata dalla MainActivity piuttosto che
                dalla ListaIndicatoriActivity, ma non ci interessa in questo punto del "percorso"*/
                idIndicatoreSelezionato =
                        savedInstanceState.getString(Costanti.ID_INDICATORE_SELEZIONATO,
                        "File non esiste");
                caricaLayoutLista();
                break;
            }
            /*altrimenti se è == null, o è stata lanciata da 1 altra attività, oppure come sopra ma
            il s.o. non gli ha passato l'oggetto Bundle*/
            /*Per vedere quale caso è ottengo l'intent ricevuto dall'attività genitore e ne
            estrapolo l'oggetto bundle contenente i dati passati*/
            else {
                intent_prec = getIntent();      /*ritorna l'intento che ha avviato questa activity*/
                bundle = intent_prec.getExtras();
                /*se null significa che l'attività è stata ripresa (per esempio l'utente torna da
                quella successiva) e non lanciata da quella precedente, quindi carico in memoria i
                dati dalle preferenze condivise precedentemente salvate*/
                if (bundle == null) {
                    SharedPreferences sharedPreferences =
                            getSharedPreferences(Costanti.PREFERENCES_FILE_PAESI,
                                    Context.MODE_PRIVATE);
                    json_file = sharedPreferences.getString(Costanti.KEY_JSON_FILE_COUNTRY,
                            "File non esiste");
                    nome_classe_selezionata =
                            sharedPreferences.getString(Costanti.NOME_CLASSE_SELEZIONATA,
                                    "File non esiste");
                    /*può tornare null e lanciare eccezione a runtime se l'attività è stata lanciata
                     dalla MainActivity piuttosto che dalla ListaIndicatoriActivity*/
                    if(sharedPreferences.contains(Costanti.ID_INDICATORE_SELEZIONATO)) {
                        idIndicatoreSelezionato =
                                sharedPreferences.getString(Costanti.ID_INDICATORE_SELEZIONATO,
                                        "File non esiste");
                    }

                    caricaLayoutLista();
                    break;

                }
                /*altrimenti è stata lanciata da 1 attività precedente: nè recupero i dati del
                bundle ricevuto e scarico il file json*/
                else {
                    nome_classe_selezionata =
                            bundle.getString(Costanti.NOME_CLASSE_SELEZIONATA);
                    /*può tornare null se l'attività è stata lanciata dalla MainActivity piuttosto
                    che dalla ListaIndicatoriActivity, ma non ci interessa in questo punto del
                    "percorso"*/
                    idIndicatoreSelezionato =
                            bundle.getString(Costanti.ID_INDICATORE_SELEZIONATO);
                    /*scarica la lista dei Paesi json e trasformali in List<Paese> con GSON*/
                    new DownloadFileTask().execute();
                    break;
                }

            }
        }/*chiude for*/

    }

    /*riceve il file json, lo trasforma con GSON in una List<Paese>, e collega quest'ultima alla
    listView tramite l'adattatore che instanzia*/
    private void caricaLayoutLista(){
        /*DEBUG*/
        Log.d(Costanti.NOME_APP + "JSON FILE ", json_file);

        /*con la libreria GSON ottengo la corrispondente lista/array di paesi del file json*/
        MyGSON myGSON = new MyGSON();
        lista_paesi = myGSON.getListPaesi(json_file);

        /*DEBUG*/
        Log.d(Costanti.NOME_APP + " DIM LISTA ", String.valueOf(lista_paesi.size()));
        for (int i = 0; i < lista_paesi.size(); i++)
            Log.d(Costanti.NOME_APP, lista_paesi.get(i).toString() + "\n");

        /*l'adattatore prende i dati dalla lista e li passa alla vista*/
        paesi_adapter = new PaesiAdapter(getApplicationContext(), R.layout.riga_layout,
                lista_paesi);
        listView.setAdapter(paesi_adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return false;
    }


    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Costanti.KEY_JSON_FILE_COUNTRY, json_file);
        savedInstanceState.putString(Costanti.NOME_CLASSE_SELEZIONATA, nome_classe_selezionata);
        savedInstanceState.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);

    }



    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua, l'oggetto Bundle salvato
    in onSaveInstanceState() non viene salvato. O meglio, non mi viene passato in Oncreate().
    La guida dice che se l'attività viene distrutta per vincoli di sistema, il s.o. dovrebbe, ma
    non è sicuro, ripristinare (e quindi passando il Bundle) e non crerae una nuova istanza.*/
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref =
                getSharedPreferences(Costanti.PREFERENCES_FILE_PAESI, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Costanti.KEY_JSON_FILE_COUNTRY, json_file);
        editor.putString(Costanti.NOME_CLASSE_SELEZIONATA, nome_classe_selezionata);
        editor.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);

        editor.apply();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        bundle = new Bundle();
        bundle.putString(Costanti.ID_PAESE_SELEZIONATO, lista_paesi.get(position++).getId());
        bundle.putString(Costanti.NOME_CLASSE_SELEZIONATA, nome_classe_selezionata );
        bundle.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);

        Class<?> classe = ListaPaesiActivity.class;
        if(nome_classe_selezionata.contentEquals(classe.getName()))
            intent_succ = new Intent(getApplicationContext(), ListaArgomentiActivity.class);
        else
            intent_succ = new Intent(getApplicationContext(), GraficoActivity.class);

        intent_succ.putExtras(bundle);
        startActivity(intent_succ);
    }


    /*thread che in background scarica in una stringa il file json dei paesi*/
    private class DownloadFileTask extends AsyncTask<Void, Void, String> {

        private InputStream risposta;
        private StringBuilder sb;
        private HttpURLConnection client;

        @Override
        protected String doInBackground(Void... voids) {

            try {
                url = new URL(Costanti.API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500);
                /*creo l'oggetto HttpURLConnection e apro la connessione al server*/
                client = (HttpURLConnection) url.openConnection();

                /*Recupero le informazioni inviate dal server*/
                risposta = new BufferedInputStream(client.getInputStream());

                /*leggo i caratteri e li appendo in sb*/
                sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(risposta));
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }
            }
            /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
            catch (MalformedURLException e) {
                    Log.d(Costanti.NOME_APP, e.getMessage());
            }

            catch (IOException e) {
                Log.d(Costanti.NOME_APP, e.getMessage());

            } finally {
                client.disconnect();
            }

            /*convert StringBuilder to String using toString() method*/
            return sb.toString();
        }


        protected void onPostExecute(String risultato) {

            json_file = risultato;
            caricaLayoutLista();
        }
    }


}
