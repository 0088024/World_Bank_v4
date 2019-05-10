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


/*activity che carica e visualizza le n-tuple salvate nel database*/
public class CaricaDati extends AppCompatActivity implements View.OnClickListener {


    private DbManager dbManager;
    private CursorAdapter cursorAdapter;
    private int position;
    private long id_record;

    private ListView listView;
    private ProgressBar progressBar;
    private Intent intent;
    private Bundle bundle;
    private Cursor cursor;



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

        progressBar = findViewById(R.id.progressBar4);
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
        Intent intent=new Intent();
        setResult(RESULT_OK,intent); // Informa l'attività chiamante con un codice
        finish();
        return false;
    }



    /*chiamato dal s.o. quando viene cliccato uno dei bottoni di ogni singola riga del layout*/
    @Override
    public void onClick(View v) {

        /*ritorna la posizione della vista con il bottone cliccato */
        position = listView.getPositionForView(v);
        /*ritorna l'id del record corrispondente alla vista con il bottone cliccato*/
        id_record = cursorAdapter.getItemId(position);

        if (v.getId() == R.id.imageButtonDelete) {
            /* Mostra una alert Dialog per confermare l'operazione */
            DialogDeleteRow mydialog = new DialogDeleteRow();
            mydialog.show(getSupportFragmentManager(), "mydialog");

            String str = mydialog.getScelta_utente();
            if(str == null)
             Log.d(Costanti.NOME_APP, null);
            dbManager.delete(id_record);
            cursorAdapter.changeCursor(dbManager.query());
        }

        if(v.getId() == R.id.imageButtonDati){
            intent = new Intent(this, VisualizzaDati.class);
            bundle = new Bundle();
            bundle.putLong(Costanti.ID_RECORD_TABELLA, id_record);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }




    /*thread che in background carica i dati dal database locale*/
    private class CaricaDatabaseTask extends AsyncTask< Void, Integer, Cursor > {

        private DbManager dbManager;
        private int count=1;


        public CaricaDatabaseTask(){

            this.dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                database*/
            setDbManager(dbManager);        /*passa alla classe contenitore un riferimento al
                                            dbManager così lo potrà chiudere nella onDestroy()*/
        }


        @Override
        protected Cursor doInBackground(Void... params) {

            Cursor cursor = dbManager.query();
            setCursor(cursor);

            /*Fammi vedere per un certo tempo stabilito da una costante la Progress Bar*/
            for (; count<= Costanti.progressBarTime;count++)
                publishProgress(count);

            return cursor;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected void onPostExecute(Cursor cursorRisultato){
            Log.d(Costanti.NOME_APP , ": CURSORE -->  "+ showCursor(cursorRisultato));
            progressBar.setVisibility(View.GONE);
            /* Controlla se la query ha prodotto nessun risultato */
            if(cursorRisultato.getCount()==0){
                Intent intent=new Intent();
                setResult(RESULT_CANCELED,intent);  /*Informa l'attività chiamante con un codice*/
                finish();                           /*Non si può proseguire*/
            }
            else
                caricaLayout(cursorRisultato);
        }


    }




    private void caricaLayout(Cursor cursorRisultato){

        cursorAdapter = new MyCursorAdapter(this, cursorRisultato, 0,
                this);

        listView.setAdapter(cursorAdapter);
    }



    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare. Tuttavia forse è meglio rilasciarlo qui le risorse perchè in caso di
    poca memoria la onDestroy() potrebbe non essere chiamata*/
    @Override
    protected void onPause(){
        super.onPause();
        cursor.close();
        dbManager.close();
    }


    /*se le risorse sono aperte, le chiude*/
    @Override
    protected void onDestroy(){
        if(!cursor.isClosed())
            cursor.close();
        if(!dbManager.isClosed())
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
