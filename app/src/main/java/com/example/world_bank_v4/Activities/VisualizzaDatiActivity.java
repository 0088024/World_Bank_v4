package com.example.world_bank_v4.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.world_bank_v4.Controller.DbManager;
import com.example.world_bank_v4.Dialog.DialogDataMissing;
import com.example.world_bank_v4.R;

public class VisualizzaDatiActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private DbManager dbManager;
    private Cursor cursor;
    private Intent intent;
    private Bundle bundle;
    private long id_record;
    private Bundle savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_visualizza_dati);

        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": CREATE");
    }



    /*ripristina lo stato dell'istanza precedentemente salvato nel Bundle ora ricevuto dal S.O.
    Quest'ultimo chiama questo metodo solo se bundle != null */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": RESTORE_INSTANCE_STATE");

        /*se è != null significa che l'attività (non è stata lanciata da 1 altra attività, ma)
        è stata ripresa (per esempio l'utente torna da quella successiva) e/o reistanziata causa
        vincoli di integrità, e inoltre il s.o. ha passato l'oggetto bundle salvato in
        precedenza in onSaveInstancestate()*/
        id_record = savedInstanceState.getLong(getResources().getString(R.string.ID_RECORD_TABELLA));
    }




    @Override
    public void onResume() {
        super.onResume();
        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.drawable.indicator);

        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP), this.getClass().getCanonicalName() + ": RESUME");
        progressBar = findViewById(R.id.progressBar);

        /*se è stata lanciata da CaricaDati*/
        intent = getIntent();
        bundle = intent.getExtras();
        if(bundle != null) {
            id_record = bundle.getLong(res.getString(R.string.ID_RECORD_TABELLA));
            Log.d(getResources().getString(R.string.NOME_APP), "recuperati dati da bundle_prec");
        }
        /*altrimenti se non è stata lanciata da CaricaDati ma ripresa dopo onPause()*/
        else {
            /*e non ha ricevuto il Bundle in onRestoreInstanceState(), deve recuperare da disco
            le varibili di stato*/
            if(savedInstanceState == null) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences(res.getString(R.string.PREFERENCES_FILE_VISUALIZZA_DATI),
                                                        Context.MODE_PRIVATE);
                id_record =
                        sharedPreferences.getLong(res.getString(R.string.KEY_RECORD_ID), 0);
                Log.d(res.getString(R.string.NOME_APP), "recuperati dati da disco");
            }
            /*se invece ha già ricevuto il bundle dal S.O., le varibili le ha già recuperate in
            onRestoreInstanceState()*/
        }
        new CaricaDatabaseTask(id_record).execute();

    }



    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": RESTART");
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(getResources().getString(R.string.NOME_APP), this.getClass().getCanonicalName() +
                ": SAVE_INSTANCE_STATE");
        savedInstanceState.putLong(getResources().getString(R.string.ID_RECORD_TABELLA), id_record);
    }



    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare. Tuttavia forse è meglio rilasciarlo qui le risorse perchè in caso di
    poca memoria la onDestroy() potrebbe non essere chiamata*/
    @Override
    protected void onPause(){
        super.onPause();
        Resources res = getResources();
        Log.d(res.getString(R.string.NOME_APP), this.getClass().getCanonicalName() + ": PAUSE");
        SharedPreferences sharedPref =
                getSharedPreferences(res.getString(R.string.PREFERENCES_FILE_VISUALIZZA_DATI),
                        Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(res.getString(R.string.KEY_RECORD_ID), id_record);
        editor.apply();
    }



    /*se le risorse sono aperte, le chiude*/
    @Override
    protected void onDestroy(){
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": DESTROY");
        if(dbManager != null)  /*potrebbe essere null se non è stato mai aperto in GraficoActivity e
                               l'utente torna indietro.*/
            dbManager.close();

        if(cursor != null)
            cursor.close();
        super.onDestroy();

    }


    /*thread che in background carica i dati dal database locale*/
    private class CaricaDatabaseTask extends AsyncTask< Void, Integer, Cursor > {

        private DbManager dbManager;
        private int count=1;
        private long id;


        public CaricaDatabaseTask(long id){
            this.id = id;
            this.dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            setDbManager(dbManager);        /*passa alla classe contenitore un riferimento al
                                            dbManager così lo potrà chiudere nella onDestroy()*/
        }


        @Override
        protected Cursor doInBackground(Void... params) {

            Cursor cursor = dbManager.query(id);
            setCursor(cursor);

            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (; count<= getResources().getInteger(R.integer.progressBarTime); count++)
                publishProgress(count);

            return cursor;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }



        protected void onPostExecute(Cursor cursorRisultato) {
            Log.d(getResources().getString(R.string.NOME_APP),
                    ": CURSORE -->  " + dbManager.showCursor(cursorRisultato));
            progressBar.setVisibility(View.GONE);
            /* Controlla se la query ha prodotto nessun risultato */
            /*if (cursorRisultato.getCount() == 0) {

                DialogDataMissing mydialog = new DialogDataMissing();
                mydialog.show(getSupportFragmentManager(),
                        getResources().getString(R.string.MY_DIALOG));
                /*Intent intent = new Intent();
                setResult(RESULT_FIRST_USER, intent); // Informa l'attività chiamante con un codice
                finish(); // Non si può proseguire
                return;

            }*/

            String[] nomi_colonne =
                    getResources().getStringArray(R.array.nomi_colonne_visualizza_dati);
            /*costruisci le righe della tabella layout riempendole con i valori del record del
            database*/
            cursorRisultato.moveToFirst();
            TableRow inflateRow;

            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
            int num_colonne_database = cursorRisultato.getColumnCount();
            int a = 0;
            for (int i = 0; i < num_colonne_database + 1 ; i++, a++) {
                inflateRow = (TableRow) View.inflate(getApplicationContext(), R.layout.table_row,
                        null);

                //set tag for each TableRow
                inflateRow.setTag(i);

                TextView txv = inflateRow.findViewById(R.id.textViewColonna);
                txv.setText(nomi_colonne[i]);
                txv = inflateRow.findViewById(R.id.textViewValore);
                if(i == 8) {
                    txv.setText("VALUES");
                    txv.setTextColor(Color.BLACK);
                    a--;
                }
                else{ txv.setText(cursorRisultato.getString(a));}

                //add TableRows to TableLayout
                tableLayout.addView(inflateRow);
            }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item){        
        finish();
        return false;
    }



    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }

    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }


}
