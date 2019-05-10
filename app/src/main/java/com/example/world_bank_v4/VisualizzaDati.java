package com.example.world_bank_v4;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VisualizzaDati extends AppCompatActivity {

    private ProgressBar progressBar;
    private DbManager dbManager;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_dati);

        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        progressBar = findViewById(R.id.progressBar6);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        new CaricaDatabaseTask(bundle.getLong(Costanti.ID_RECORD_TABELLA)).execute();

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
                setResult(RESULT_CANCELED, intent); // Informa l'attività chiamante con un codice
                finish(); // Non si può proseguire
                return;

            }

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



    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare*/
    @Override
    protected void onDestroy(){
        cursor.close();
        dbManager.close();
        super.onDestroy();

    }



    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }

    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }


}
