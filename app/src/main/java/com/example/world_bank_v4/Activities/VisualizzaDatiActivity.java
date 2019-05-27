package com.example.world_bank_v4.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.world_bank_v4.Controller.DbManager;
import com.example.world_bank_v4.Model.Costanti;
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
        setContentView(R.layout.activity_visualizza_dati);
        this.savedInstanceState = savedInstanceState;
        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": CREATE");
    }



    /*ripristina lo stato dell'istanza precedentemente salvato nel Bundle ora ricevuto dal S.O.
    Quest'ultimo chiama questo metodo solo se bundle != null */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(Costanti.NOME_APP,
                this.getClass().getCanonicalName() + ": RESTORE_INSTANCE_STATE");

        /*se è != null significa che l'attività (non è stata lanciata da 1 altra attività, ma)
        è stata ripresa (per esempio l'utente torna da quella successiva) e/o reistanziata causa
        vincoli di integrità, e inoltre il s.o. ha passato l'oggetto bundle salvato in
        precedenza in onSaveInstancestate()*/
        id_record = savedInstanceState.getLong(Costanti.ID_RECORD_TABELLA);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": RESUME");
        progressBar = findViewById(R.id.progressBar);
        /*se non è stata lanciata da CaricaDati ma ripresa dopo onPause(), deve recuperare le
        variabili d'istanza da disco*/
        if(savedInstanceState == null){
            /*carica dati da disco*/
        }
        else{
            intent = getIntent();
            bundle = intent.getExtras();
            id_record = bundle.getLong(Costanti.ID_RECORD_TABELLA);
            new CaricaDatabaseTask(id_record).execute();
        }
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
            for (; count<= Costanti.progressBarTime;count++)
                publishProgress(count);

            return cursor;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }



        protected void onPostExecute(Cursor cursorRisultato) {
            Log.d(Costanti.NOME_APP, ": CURSORE -->  " + showCursor(cursorRisultato));
            progressBar.setVisibility(View.GONE);
            /* Controlla se la query ha prodotto nessun risultato */
            if (cursorRisultato.getCount() == 0) {
                Intent intent = new Intent();
                setResult(RESULT_FIRST_USER, intent); // Informa l'attività chiamante con un codice
                finish(); // Non si può proseguire
                return;

            }

            /*costruisci le righe della tabella layout riempendole con i valori del record del
            database*/
            cursorRisultato.moveToFirst();

            TableRow inflateRow;

            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

            for (int i = 0; i < cursorRisultato.getColumnCount(); i++) {

                inflateRow = (TableRow) View.inflate(getApplicationContext(), R.layout.table_row,
                        null);

                //set tag for each TableRow
                inflateRow.setTag(i);

                TextView txv = inflateRow.findViewById(R.id.textViewColonna);
                txv.setText(cursorRisultato.getColumnName(i));
                txv = inflateRow.findViewById(R.id.textViewValore);
                txv.setText(cursorRisultato.getString(i));

                //add TableRows to TableLayout
                tableLayout.addView(inflateRow);
            }
        }
    }




    /*restituisce 1 stringa che mostra il contenuto di tutte le colonne del record puntato dal
   cursore*/
    private String showCursor(Cursor cursor) {
        cursor.moveToPosition(-1);
        String cursorData = "\nCursor: [";
        try {
            String[] colName = cursor.getColumnNames();
            for(int i=0; i<colName.length; i++) {
                String dataType = getColumnType(cursor, i);
                cursorData += colName[i] + dataType;
                if (i<colName.length-1)
                    cursorData+= ", ";
            }
        } catch (Exception e) {
            Log.e( "<<SCHEMA>>" , e.getMessage() );
        }
        cursorData += "]";
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String cursorRow = "\n[";
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                cursorRow += cursor.getString(i);
                if (i<cursor.getColumnCount()-1)
                    cursorRow += ", ";
            }
            cursorData += cursorRow + "]";
        }
        return cursorData + "\n";
    }


    /*restituisce 1 stringa con il tipo di dato della colonna con indice "i" */
    private String getColumnType(Cursor cursor, int i) {
        try {
            cursor.moveToFirst();
            int result = cursor.getType(i);
            String[] types = { ":NULL", ":INT", ":FLOAT", ":STR", ":BLOB", ":UNK" };
            cursor.moveToPosition(-1);
            return types[result];
        } catch (Exception e) {
            return " ";
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent();
        setResult(RESULT_OK,intent); // Informa l'attività chiamante con un codice
        finish();
        return false;
    }








    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": SAVE_INSTANCE_STATE");
        savedInstanceState.putLong(Costanti.ID_RECORD_TABELLA, id_record);
    }



    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare. Tuttavia forse è meglio rilasciarlo qui le risorse perchè in caso di
    poca memoria la onDestroy() potrebbe non essere chiamata*/
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": PAUSE");
    }


    /*se le risorse sono aperte, le chiude*/
    @Override
    protected void onDestroy(){
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": DESTROY");
        dbManager.close();
        if(!cursor.isClosed())
            cursor.close();
        super.onDestroy();
    }



    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }

    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }


}