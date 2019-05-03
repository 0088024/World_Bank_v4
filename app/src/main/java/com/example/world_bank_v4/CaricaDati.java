package com.example.world_bank_v4;


import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/*activity che carica e visualizza le n-tuple salvate nel database*/
public class CaricaDati extends AppCompatActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {


    private DbManager dbManager;
    private ListView listView;
    private CursorAdapter cursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carica_dati);
         /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.indicator);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        listView = findViewById(R.id.list_view_carica_dati);
        dbManager = new DbManager(this);
        new CaricaDatabaseTask(dbManager).execute();

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




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }



    @Override
    public void onClick(View v) {
        int position = listView.getPositionForView(v);
        long id = cursorAdapter.getItemId(position);
        if (dbManager.delete(id))
            cursorAdapter.changeCursor(dbManager.query());
    }





    /*thread che in background carica i dati dal database locale*/
    private class CaricaDatabaseTask extends AsyncTask< Void, Void, Cursor > {

        private DbManager dbManager;


        public CaricaDatabaseTask(DbManager dbManager){
            this.dbManager = dbManager;
        }


        @Override
        protected Cursor doInBackground(Void... params) {

            dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            Cursor cursor = dbManager.query();

            return cursor;
        }



        protected void onPostExecute(Cursor cursorRisultato){
            Log.d(Costanti.NOME_APP , ": CURSORE -->  "+ showCursor(cursorRisultato));

            cursorAdapter = new MyCursorAdapter(getApplicationContext(), cursorRisultato, 0,
                    getThis());
            listView.setAdapter(cursorAdapter);
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


    public CaricaDati getThis() {return  this;}
}
