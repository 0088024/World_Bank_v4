package com.example.world_bank_v4.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.world_bank_v4.Dialog.DialogNoCountry;
import com.example.world_bank_v4.Dialog.DialogNoIndicator;
import com.example.world_bank_v4.Controller.MyGSON;
import com.example.world_bank_v4.R;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListaGenericaActivity extends AppCompatActivity implements
                                                        AdapterView.OnItemClickListener {

    private URL url;
    private ListView listView;
    private int idListView;
    private ArrayList lista_oggetti;
    private ArrayAdapter adapter;
    private Intent intent_prec;
    private Bundle bundle_prec;
    private Bundle bundle_succ;
    private String json_file;
    private String messag_notification;
    private String nomeClasseSelezionata;
    private String idIndicatoreSelezionato;
    private String nomeIndicatoreSelezionato;
    private String idArgomentoSelezionato;
    private String idPaeseSelezionato;
    private String nomePaeseSelezionato;
    private String KEY_JSON_FILE;
    private String NOME_FILE_PREFERENCES;
    private String API_WORLD_BANK;
    private Integer requestCode;
    private TypeToken typeToken;
    private Bundle savedInstanceState;
    private ProgressBar progressBar;
    private boolean returningWithResult;
    private boolean lanciata_da_precedente = false;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": CREATE");

        /*se savedInstanceState è == null, o è stata lanciata da 1 altra attività, oppure è
        stata ripresa ma il s.o. non gli ha passato l'oggetto Bundle*/
        if (savedInstanceState == null) {
            /*Per vedere quale caso è, ottengo l'intent ricevuto dall'attività genitore e ne
            estrapolo l'oggetto bundle contenente i dati passati dalla precedente (se presenti)*/
            intent_prec = getIntent();   /*ritorna l'intento che ha avviato questa activity*/
            bundle_prec = intent_prec.getExtras();
            int chiamante = 0;
            if (bundle_prec != null) {
                chiamante =
                        bundle_prec.getInt(getResources().getString(R.string.ATTIVITÀ_LANCIATA));
            }
            /*se == 1 significa che l'attività è stata lanciata da quella precedente, quindi
            recupero i dati che l'attività precedente mi ha passato nel bundle*/
            if (chiamante == 1){
                Log.d(res.getString(R.string.NOME_APP),
                        this.getClass().getCanonicalName() +
                                ": getStateFromBundle(bundle_prec)");

                /*recupero le variabili di stato dal bundle ricevuto dall'attività precedente*/
                lanciata_da_precedente = getStateFromBundle(bundle_prec);
                Log.d(res.getString(R.string.NOME_APP),
                        this.getClass().getCanonicalName() +
                        ": lanciata_da_precedente: " + lanciata_da_precedente );

                bundle_prec.clear();        /*"resetto" il bundle precedente*/
                Log.d(res.getString(R.string.NOME_APP),
                        this.getClass().getCanonicalName() +
                        ": bundle_prec.getInt(Prova) dopo bundle_prec.clear() = "
                        + bundle_prec.getInt("Prova"));

            }
            /*altrimenti significa che è stata ripresa dall'utente e ricreata dal S.O. (causa
            vincoli di sistema). In questo caso se il S.O. è riuscito o meno a passarci il
            savedInstanceState lo controlliamo in onRestoreInstancestate*/
            else lanciata_da_precedente = false;
        }
    }



    /*ripristina lo stato dell'istanza precedentemente salvato nel Bundle ora ricevuto dal S.O.
    Quest'ultimo chiama questo metodo solo se bundle è != null
    Se è != null significa che l'attività (non è stata lanciata da 1 altra attività, ma)
    è stata ripresa (per esempio l'utente torna da quella successiva) e/o reistanziata causa
    vincoli di integrità, e inoltre il s.o. ha passato l'oggetto bundle salvato in
    precedenza in onSaveInstancestate()*/
    @Override
    public void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": RESTORE_INSTANCE_STATE");
        /*causa eccessiva dimensione di alcune liste di indicatori, tali da generare ExceptionBundle
        size, l'activity ListaIndicatori non salva lo stato dell'istanza nel bundle, ma solo sul
        disco*/
        if(this.getClass().getCanonicalName() == ListaIndicatoriActivity.class.getCanonicalName()){
            Log.d(getResources().getString(R.string.NOME_APP),
                    this.getClass().getCanonicalName() + ": No restore Bundle");
            return ;
        }
        /*memorizzo il bundle ricevuto*/
        this.savedInstanceState = bundle;
    }




    /*qui vengono recuperati i dati o dal sito e dal disco. Vengono recuperati qui perchè questo
    metodo viene chiamato sia quando l'attività è creata che quando è ripresa dopo onPause() e/o
    onStop()*/
    @Override
    public void onResume(){
        super.onResume();

        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": RESUME");
        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setProgressBarNoVisible();

        caricaVariabili();

        /*per evitare la perdita di stato dell'attività la transazione viene eseguita soltanto dopo
        che l'attività è stata ripristinata allo stato originale*/
        if (returningWithResult == true &&
                requestCode == res.getInteger(R.integer.LISTA_PAESI_CODE)) {
            // Commit your transactions here.
            DialogNoCountry mydialog = new DialogNoCountry();
            mydialog.show(getSupportFragmentManager(),"mydialog");
        }
        if (returningWithResult == true &&
                requestCode == res.getInteger(R.integer.LISTA_INDICATORI_CODE)) {
            // Commit your transactions here.
            DialogNoIndicator mydialog = new DialogNoIndicator();
            mydialog.show(getSupportFragmentManager(),"mydialog");
        }
        // Reset the boolean flag back to false for next time.
        returningWithResult = false;
    }



    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": RESTART");
        lanciata_da_precedente = false;

    }



    /*serve x salvare in un oggetto Bundle di sistema il file json*. E' chiamato dal sistema
    prima di far entrare l'attività in onPause(). Se però l'attività è chiusa esplicitamente
    dall'utente (con il tasto indietro per esempio) non viene chiamato dal sistema*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /*causa eccessiva dimensione di alcune liste di indicatori, tali da generare ExceptionBundle
        size, l'activity ListaIndicatori non salva lo stato dell'istanza nel bundle, ma solo sul
        disco*/
        if(this.getClass().getCanonicalName() == ListaIndicatoriActivity.class.getCanonicalName())
            return;
        Resources res = getResources();

        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": SAVE_INSTANCE_STATE");

        savedInstanceState.putString(res.getString(R.string.NOME_CHIAVE_FILE_JSON), KEY_JSON_FILE);
        savedInstanceState.putString(KEY_JSON_FILE, json_file);

        savedInstanceState.putString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                nomeClasseSelezionata);
        savedInstanceState.putString(res.getString(R.string.ID_INDICATORE_SELEZIONATO),
                idIndicatoreSelezionato);
        savedInstanceState.putString(res.getString(R.string.NOME_INDICATORE_SELEZIONATO),
                nomeIndicatoreSelezionato);
        savedInstanceState.putString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO),
                idArgomentoSelezionato);
        savedInstanceState.putString(res.getString(R.string.ID_PAESE_SELEZIONATO),
                idPaeseSelezionato);
        savedInstanceState.putString(res.getString(R.string.NOME_PAESE_SELEZIONATO),
                nomePaeseSelezionato);

        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": Bundle savedInstanceState salvato");

        super.onSaveInstanceState(savedInstanceState);

    }



    /*unico metodo sicuro per salvare dati: se infatti non li salvo qua, l'oggetto Bundle salvato
    in onSaveInstanceState() non viene salvato. O meglio, non mi viene passato in Oncreate().
    La guida dice che se l'attività viene distrutta per vincoli di sistema, il s.o. dovrebbe, ma
    non è sicuro, ripristinare (e quindi passando il Bundle) e non crerae una nuova istanza.*/
    @Override
    public void onPause(){
        super.onPause();
        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": PAUSE");
        SharedPreferences sharedPref =
                getSharedPreferences(NOME_FILE_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(res.getString(R.string.NOME_CHIAVE_FILE_JSON), KEY_JSON_FILE);
        editor.putString(KEY_JSON_FILE, json_file);

        editor.putString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                nomeClasseSelezionata);
        editor.putString(res.getString(R.string.ID_INDICATORE_SELEZIONATO),
                idIndicatoreSelezionato);
        editor.putString(res.getString(R.string.NOME_INDICATORE_SELEZIONATO),
                nomeIndicatoreSelezionato);
        editor.putString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO), idArgomentoSelezionato);
        editor.putString(res.getString(R.string.ID_PAESE_SELEZIONATO), idPaeseSelezionato);
        editor.putString(res.getString(R.string.NOME_PAESE_SELEZIONATO), nomePaeseSelezionato);

        editor.apply(); /*mettere commit??? dopo aver parlato con il Prof???????*/
    }


    @Override
    public void onStop(){
        super.onStop();
                  Log.d(getResources().getString(R.string.NOME_APP),
                          this.getClass().getCanonicalName() + ": STOP");
    }


    /*richiamato giusto prima che l’activity venga distrutta.Se la memoria e’ poca, il metodo NON
    verra’ richiamato e Android killera’ il processo associato all’applicazione*/
    @Override
    protected void onDestroy(){
        Log.d(getResources().getString(R.string.NOME_APP), this.getClass().getCanonicalName()
                + ": DESTROY");
        super.onDestroy();
    }




    /*ottiene dal sito o dal disco, o dal bundle prima salvato, i dati che occorrono a riempire
    la ListView, e li collega a quest'ultima*/
    public void caricaVariabili() {
        /*se l'attività non è stata lanciata da quella precedente vuol dire che è stata ripresa dall
        utente: in questo caso se savedInstanceState è stato ripristinato carico i dati da lui,
        altrimenti da disco*/
        if(lanciata_da_precedente == false) {
            /*inoltre se savedInstanceState è == false, significa che è stata ripresa ma il s.o. non
            gli ha passato l'oggetto Bundle in onRestoreInstanceState, quindi li carico da disco*/
            if (savedInstanceState == null) {
                Log.d(getResources().getString(R.string.NOME_APP), this.getClass().getCanonicalName() +
                        ": getStateFromSharedPreferences()");
                getStateFromSharedPreferences(); /*recupero le variabili di stato da disco*/
            }
            /*altrimenti se savedInstanceState è == true, è stata ripresa e il s.o. gli ha anche
            passato l'oggetto Bundle in onRestoreInstanceState(): x cui recuperiamo le variabili
            da tale bundle*/
            else {
                Log.d(getResources().getString(R.string.NOME_APP), this.getClass().getCanonicalName() +
                        ": getStateFromBundle(savedInstanceState);");
                getStateFromBundle(savedInstanceState);
            }
            /*una volta recuperate le variabili, carichiamo il layouy*/
            caricaLayout();

        }
        /*se invece è stata lanciata da una attività precedente scarica il file json relativo*/
        else new DownloadFileTask(this.getClass().getCanonicalName()).execute();

    }



    /*se non sono presenti errori il file json viene trasformato con GSON in una List<T>,
    e collega quest'ultima alla listView tramite l'adattatore che instanzia*/
    protected void caricaLayout(){

        /*con la libreria GSON ottengo la corrispondente lista/array di oggetti del file json*/
        MyGSON myGSON = new MyGSON(this);
        lista_oggetti = myGSON.getListFromJson(json_file, typeToken);
        /*DEBUG*/
        Log.d(getResources().getString(R.string.NOME_APP) + "JSON FILE ", json_file);

        listView = findViewById(idListView);
        listView.setOnItemClickListener(this);

        instanziaAdapter();
        listView.setAdapter(adapter);

    }




    /*se l'utente preme il pulsante indietro l'attività viene semplicemente terminata ed espulsa
   dallo stack activity, in modo da far tornare in 1°piano quella che l'aveva lanciata*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return false;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bundle_succ = new Bundle();
        bundle_succ.putString(getResources().getString(R.string.NOME_CLASSE_SELEZIONATA),
                nomeClasseSelezionata);
    }



    /*thread che in background scarica da internet in una stringa il file json di pertinenza*/
    private class DownloadFileTask extends AsyncTask<Void, Integer, String> {

        private InputStream risposta;
        private StringBuilder sb;
        private HttpURLConnection client;
        private int count;
        private String nameClass;
        private String risultato;


        public DownloadFileTask(String nameClass)
        {
            API_WORLD_BANK = costruisciApi();
            this.nameClass = nameClass;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Resources res = getResources();
            try {
                url = new URL(API_WORLD_BANK);
                /*creo l'oggetto HttpURLConnection e apro la connessione al server*/
                /*se il timeout era già scaduto quando riprova a connettermi qui già mi lancia
                l'eccezione "no address hostname....." senza riavviare il timeout*/
                client = (HttpURLConnection) url.openConnection();
                /*Timeout in millisecondi per la lettura*/
                client.setReadTimeout(res.getInteger(R.integer.TIMEOUT));
                risposta = new BufferedInputStream(client.getInputStream());
                /*leggo i caratteri e li appendo in sb*/
                sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(risposta));
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }

                /*convert StringBuilder to String using toString() method*/
                risultato = sb.toString();

            }

            /*decidiamo di mostrare un unico messaggio d'errore per tutti i tipi di eccezione che
            possono essere sollevati*/
            catch (IOException e) {
                Log.d(res.getString(R.string.NOME_APP),"IOException: " +e.getMessage() );
                /*imposta il messaggio d'errore da mostrare all'utente con Notificationactivity*/
                messag_notification = res.getString(R.string.IO_ERROR)
                        + res.getString(R.string.WORLDBANK_SITE);
                risultato = null;

            }

            finally {

                client.disconnect();
            }

            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (count = 0; count <= res.getInteger(R.integer.progressBarTime); count++) {
                publishProgress(count);
            }



            return risultato;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            setProgressBarVisible();   /*Attiva la progressBar*/
        }


        @Override
        protected void onPostExecute(String risultato) {
            Resources res = getResources();
            setProgressBarGone();  /*Sopprimi la progressBar*/
            /*se è != null significa che non abbiamo avuto errori nello scaricare il file*/
            if(risultato != null) {
                Log.d(res.getString(R.string.NOME_APP),
                        nameClass + ": Download file completed");
                json_file = risultato;
                caricaLayout();
            }
            /*se invece abbiamo avuto errori lo notifichiamo all'utente*/
            else{
                Log.d(res.getString(R.string.NOME_APP),
                        nameClass + res.getString(R.string.MSG_ERRORE_CONNESSIONE));
                // Errore imprevisto ad es. viene a mancare la connessione a internet
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("error", messag_notification);
                intent.putExtras(bundle);
                /*lancia la NotificationActivity richiedendone in codice di chiusura*/
                startActivityForResult(intent,
                        res.getInteger(R.integer.RETURN_FROM_NOTIFICATION_ACTIVITY));
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": ON_ACTIVITY_RESULT");

        this.requestCode = requestCode;
        returningWithResult = true;

        /*Controllo dei codici di risposta delle attività lanciate*/
        if(resultCode == res.getInteger(R.integer.NO_DATA)){
            /*Errore previsto ad es. nessun dato disponibile per un certo paese*/
            returningWithResult = true;
        }

        /*se l'attività da cui ritorno era la NotificationActivity allora termino per dar recuperare
        dal back stack l'attività che mi aveva lanciato*/
        if(resultCode == res.getInteger(R.integer.RETURN_FROM_NOTIFICATION_ACTIVITY)){

            finish();
        }

        /*RESULT_CANCELED è il risultato fornito dal tasto Back nativo del telefono*/
        /*if(resultCode == RESULT_CANCELED /*&& requestCode ==
                res.getInteger(R.integer.RETURN_FROM_NOTIFICATION_ACTIVITY))*/
        /*finish();*/
    }







    /*leggi dalle SharedPreferences e imposta le relativi variabili di stato dell'istanza*/
    private void getStateFromSharedPreferences(){
        Resources res = getResources();
        SharedPreferences sharedPreferences =
                getSharedPreferences(NOME_FILE_PREFERENCES, Context.MODE_PRIVATE);

        String nome_chiave_file_json =
                sharedPreferences.getString(res.getString(R.string.NOME_CHIAVE_FILE_JSON),
                              res.getString(R.string.STRING_NOT_FOUND));

        Log.d(res.getString(R.string.NOME_APP), nome_chiave_file_json);

        json_file =  sharedPreferences.getString(nome_chiave_file_json,
                res.getString(R.string.STRING_NOT_FOUND));


        nomeClasseSelezionata =
                sharedPreferences.getString(res.getString(R.string.NOME_CLASSE_SELEZIONATA),
                             res.getString(R.string.STRING_NOT_FOUND));
                /*può tornare null e lanciare eccezione a runtime se l'attività è stata lanciata
                dalla MainActivity piuttosto che dalla ListaIndicatoriActivity*/
        if (sharedPreferences.contains(res.getString(R.string.ID_INDICATORE_SELEZIONATO))) {
            idIndicatoreSelezionato =
                    sharedPreferences.getString(res.getString(R.string.ID_INDICATORE_SELEZIONATO),
                              res.getString(R.string.STRING_NOT_FOUND));
        }
        if (sharedPreferences.contains(res.getString(R.string.NOME_INDICATORE_SELEZIONATO))) {
            nomeIndicatoreSelezionato =
                    sharedPreferences.getString(res.getString(R.string.NOME_INDICATORE_SELEZIONATO),
                             res.getString(R.string.STRING_NOT_FOUND));
        }
        if (sharedPreferences.contains(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO))) {
            idArgomentoSelezionato =
                    sharedPreferences.getString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO),
                             res.getString(R.string.STRING_NOT_FOUND));
        }
        if (sharedPreferences.contains(res.getString(R.string.ID_PAESE_SELEZIONATO))) {
            idPaeseSelezionato =
                    sharedPreferences.getString(res.getString(R.string.ID_PAESE_SELEZIONATO),
                            res.getString(R.string.STRING_NOT_FOUND));
        }
        if (sharedPreferences.contains(res.getString(R.string.NOME_PAESE_SELEZIONATO))) {
            nomePaeseSelezionato =
                    sharedPreferences.getString(res.getString(R.string.NOME_PAESE_SELEZIONATO),
                            res.getString(R.string.STRING_NOT_FOUND));
        }


    }


    /*leggi dal Bundle e imposta le relativi variabili di stato dell'istanza*/
    private boolean getStateFromBundle(Bundle bundle){
        Resources res = getResources();
        nomeClasseSelezionata =
                bundle.getString(res.getString(R.string.NOME_CLASSE_SELEZIONATA));
        /*può tornare null se l'attività è stata lanciata per esempio dalla MainActivity
        piuttosto che dalla ListaIndicatoriActivity, ma non ci interessa in questo
        punto del "percorso"*/
        idIndicatoreSelezionato =
                bundle.getString(res.getString(R.string.ID_INDICATORE_SELEZIONATO));
        nomeIndicatoreSelezionato =
                bundle.getString(res.getString(R.string.NOME_INDICATORE_SELEZIONATO));
        idArgomentoSelezionato =
                bundle.getString(res.getString(R.string.ID_ARGOMENTO_SELEZIONATO));
        idPaeseSelezionato =
                bundle.getString(res.getString(R.string.ID_PAESE_SELEZIONATO));
        nomePaeseSelezionato =
                bundle.getString(res.getString(R.string.NOME_PAESE_SELEZIONATO));
        lanciata_da_precedente =
                bundle.getBoolean(res.getString(R.string.LANCIATA_DA_PRECEDENTE));

        String nome_chiave_file_json =
                bundle.getString(res.getString(R.string.NOME_CHIAVE_FILE_JSON));
        json_file = bundle.getString(nome_chiave_file_json);
        return true;
    }


    public void setIdListView(int idListView){
        this.idListView = idListView;
    }

    public void setLista_oggetti(ArrayList lista_oggetti) {
        this.lista_oggetti = lista_oggetti;
    }

    public void setTypeToken(TypeToken typeToken) {
        this.typeToken = typeToken;
    }

    public void setAPI_WORLD_BANK(String API_WORLD_BANK) {
        this.API_WORLD_BANK = API_WORLD_BANK;
    }

    public void setKEY_JSON_FILE(String KEY_JSON_FILE) {
        this.KEY_JSON_FILE = KEY_JSON_FILE;
    }

    public void setNOME_FILE_PREFERENCES(String NOME_FILE_PREFERENCES) {
        this.NOME_FILE_PREFERENCES = NOME_FILE_PREFERENCES;
    }

    public void setAdapter(ArrayAdapter adapter) { this.adapter = adapter; }

    public void setProgressBar(int id){
        this.progressBar = findViewById(id);
    }

    protected void setProgressBarVisible(){

        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    protected void setProgressBarNoVisible(){

        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    protected void setProgressBarGone(){

        progressBar.setVisibility(View.GONE);
    }

    public String costruisciApi(){
        return "Fare override. Questo è il metodo della superclasse";
    }

    public void instanziaAdapter(){ }


    public String getJsonFile(){
        return json_file;
    }

    public String getErrorFile(){
        return messag_notification;
    }

    public String getNomeClasseSelezionata(){
        return nomeClasseSelezionata;
    }

    public String getIdIndicatoreSelezionato(){
        return idIndicatoreSelezionato;
    }

    public String getNomeIndicatoreSelezionato(){
        return nomeIndicatoreSelezionato;
    }

    public String getNomePaeseSelezionato(){
        return nomePaeseSelezionato;
    }

    public String getIdArgomentoSelezionato(){
        return idArgomentoSelezionato;
    }

    public String getIdPaeseSelezionato(){
        return idPaeseSelezionato;
    }

    public ArrayList getListaOggetti(){
        return lista_oggetti;
    }

    public ArrayAdapter getAdapter(){
        return adapter;
    }

    public ListView getListView() { return listView; }

    public Bundle getBundleSucc() { return bundle_succ; }

    public ProgressBar getProgressBar() { return progressBar; }

}


