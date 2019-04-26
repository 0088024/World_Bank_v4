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

public class GraficoActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent_prec;
    private Bundle bundle;
    private URL url;
    private String json_file;
    private String idPaeseSelezionato;
    private String idIndicatoreSelezionato;
    private DbManager dbManager;
    private ArrayList<Grafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;
    private Button button_salva_database;
    private Button button_salva_grafico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        /*in this example, a LineChart is initialized from xml*/
        chart = findViewById(R.id.chart);
        button_salva_grafico = findViewById(R.id.button_salva_grafico);
        button_salva_grafico.setOnClickListener(this);
        button_salva_database = findViewById(R.id.button_salva_database);
        button_salva_database.setOnClickListener(this);

        for (; ; ) {
            /*se non è null significa che l'attività (non è stata lanciata da 1 altra attività, ma)
            è stata ripresa (per esempio l'utente torna da quella successiva) e reistanziata causa
            vincoli di integrità, e inoltre il s.o. ha passato l'oggetto bundle salvato in
            precedenza in onSaveInstancestate()*/
            if (savedInstanceState != null) {
                json_file = savedInstanceState.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE
                                                ,"File non esiste");
                idPaeseSelezionato = savedInstanceState.getString(Costanti.ID_PAESE_SELEZIONATO);
                idIndicatoreSelezionato =
                            savedInstanceState.getString(Costanti.ID_INDICATORE_SELEZIONATO);

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
                            getSharedPreferences(Costanti.PREFERENCES_FILE_INDICATORE_PER_PAESE,
                                    Context.MODE_PRIVATE);
                    json_file =
                            sharedPreferences.getString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE,
                                    "File non esiste");
                    idIndicatoreSelezionato =
                            sharedPreferences.getString(Costanti.ID_INDICATORE_SELEZIONATO,
                                    "File non esiste");

                    if (sharedPreferences.contains(Costanti.ID_PAESE_SELEZIONATO)) {
                        idPaeseSelezionato =
                                sharedPreferences.getString(Costanti.ID_PAESE_SELEZIONATO,
                                        "File non esiste");
                    }
                    caricaLayoutLista();
                    break;

                }
                /*altrimenti è stata lanciata da 1 attività precedente: nè recupero i dati del
                bundle ricevuto e scarico il file json*/
                else {
                    idPaeseSelezionato = bundle.getString(Costanti.ID_PAESE_SELEZIONATO);
                    idIndicatoreSelezionato = bundle.getString(Costanti.ID_INDICATORE_SELEZIONATO);
                    /*scarica la lista dei valori dell'indicatore per paese json e trasformali in
                    List<T> con GSON*/
                    new DownloadFileTask().execute();
                    break;
                }

            }
        }/*chiude for*/

    }



    /*riceve il file json, lo trasforma con GSON in una List<T>, e collega quest'ultima al chart*/
    private void caricaLayoutLista(){
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



    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
   prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
   dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE, json_file);
        savedInstanceState.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);
        savedInstanceState.putString(Costanti.ID_PAESE_SELEZIONATO, idPaeseSelezionato);
    }



    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua, l'oggetto Bundle salvato
    in onSaveInstanceState() non viene salvato. O meglio, non mi viene passato in Oncreate().
    La guida dice che se l'attività viene distrutta per vincoli di sistema, il s.o. dovrebbe, ma
    non è sicuro, ripristinare (e quindi passando il Bundle) e non crerae una nuova istanza.*/
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref =
                getSharedPreferences(Costanti.PREFERENCES_FILE_INDICATORE_PER_PAESE,
                                                            Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Costanti.KEY_JSON_FILE_INDICATORE_PER_PAESE, json_file);
        editor.putString(Costanti.ID_INDICATORE_SELEZIONATO, idIndicatoreSelezionato);
        editor.putString(Costanti.ID_PAESE_SELEZIONATO, idPaeseSelezionato);

        editor.apply();
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

    /*thread che in background scarica in una stringa il file json dell'indicatore per paese, cioè
    il grafico*/
    private class DownloadFileTask extends AsyncTask<Void, Void, String> {

        private InputStream risposta;
        private StringBuilder sb;
        private HttpURLConnection client;

        @Override
        protected String doInBackground(Void... urls) {

            try {
                /*costruisci la stringa api per ottenere una lista di valori relativi
                all'indicatore per paese selezionato*/
                StringBuilder api_indicatore_per_paese = new StringBuilder();
                /*API_COUNTRY_LIST = "https://api.worldbank.org/v2/country/"*/
                api_indicatore_per_paese.append(Costanti.API_COUNTRY_LIST);
                api_indicatore_per_paese.append(idPaeseSelezionato);
                api_indicatore_per_paese.append("/indicator/");
                api_indicatore_per_paese.append(idIndicatoreSelezionato);
                api_indicatore_per_paese.append("?format=json&&per_page=10000");

                /*DEBUG*/
                Log.d(Costanti.NOME_APP + "API", api_indicatore_per_paese.toString());

                url = new URL(api_indicatore_per_paese.toString());

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
            } catch (IOException e) {
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
