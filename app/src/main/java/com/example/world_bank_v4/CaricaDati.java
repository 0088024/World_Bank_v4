package com.example.world_bank_v4;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


/*activity che carica e visualizza le n-tuple salvate nel database*/
public class CaricaDati extends AppCompatActivity implements View.OnClickListener {


    static private DbManager dbManager;
    private ListView listView;
    static private CursorAdapter cursorAdapter;
    static private int position;
    static private long id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carica_dati);
         /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        listView = findViewById(R.id.list_view_carica_dati);



        new CaricaDatabaseTask().execute();

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
        finish();
        return false;
    }



    /*chiamato dal s.o. quando viene cliccato uno dei bottoni di ogni singola riga del layout*/
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageButtonDelete) {
            /*ritorna la posizione della vista con il bottone cliccato */
            position = listView.getPositionForView(v);
            /*ritorna l'id del record corrispondente alla vista con il bottone cliccato*/
            id = cursorAdapter.getItemId(position);
            /* Mostra una alert Dialog per confermare l'operazione */
            DialogDeleteRow mydialog = new DialogDeleteRow();
            mydialog.show(getSupportFragmentManager(), "mydialog");

        }
    }
    /* chiamato se effettivamente l'utente decide di cancellare la riga */
    protected static void deleterow() {
            if (dbManager.delete(id))
                /*Change the underlying cursor to a new cursor. If there is an existing cursor it
                will be closed. Atomaticamente aggiorna anche la list view a cui è collegato
                il Cursor Adapter*/
                cursorAdapter.changeCursor(dbManager.query());

    }


    /*thread che in background carica i dati dal database locale*/
    private class CaricaDatabaseTask extends AsyncTask< Void, Void, Cursor > {

        private DbManager dbManager;


        public CaricaDatabaseTask(){

            this.dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            setDbManager(dbManager);        /*passa alla classe contenitore un riferimento al
                                            dbManager così lo potrà chiudere nella onDestroy()*/
        }


        @Override
        protected Cursor doInBackground(Void... params) {


            Cursor cursor = dbManager.query();

            return cursor;
        }



        protected void onPostExecute(Cursor cursorRisultato){
            Log.d(Costanti.NOME_APP , ": CURSORE -->  "+ showCursor(cursorRisultato));

            caricaLayout(cursorRisultato);


        }


    }




    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare*/
    @Override
    protected void onDestroy(){
        dbManager.close();
        super.onDestroy();

    }




    private void caricaLayout(Cursor cursorRisultato){


        cursorAdapter = new MyCursorAdapter(this, cursorRisultato, 0,
                this);

        listView.setAdapter(cursorAdapter);
    }




    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }

}
