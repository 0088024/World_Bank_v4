package com.example.world_bank_v4.Activities;


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

import com.example.world_bank_v4.Adapters.MyCursorAdapter;
import com.example.world_bank_v4.Controller.DbManager;
import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;
import com.example.world_bank_v4.Dialog.DialogDeleteRow;



/*activity che carica e visualizza le n-tuple salvate nel database*/
public class CaricaDatiActivity extends AppCompatActivity implements View.OnClickListener,
        DialogDeleteRow.OnClickListener {


    private DbManager dbManager;
    private Cursor cursor;
    private CursorAdapter cursorAdapter;
    private int position;
    private long id_record;
    private ListView listView;
    private ProgressBar progressBar;
    private Intent intent;
    private Bundle bundle;


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
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": CREATE");
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": RESUME");
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.list_view_carica_dati);
        new CaricaDatabaseTask().execute();
    }



    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": RESTART");
    }





    @Override
    protected void onPause(){
        super.onPause();
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": PAUSE");
    }



    /*se le risorse sono aperte, le chiude*/
    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare. Tuttavia forse è meglio rilasciarlo qui le risorse perchè in caso di
    poca memoria la onDestroy() potrebbe non essere chiamata*/
    @Override
    protected void onDestroy(){
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": DESTROY");
        if(dbManager != null)  /*potrebbe essere null se non è stato mai aperto in GraficoActivity e
                               l'utente torna indietro.*/
            dbManager.close();

        if(!cursor.isClosed())
            cursor.close();
        super.onDestroy();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item){        
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

        }

        if(v.getId() == R.id.imageButtonDati){
            intent = new Intent(this, VisualizzaDatiActivity.class);
            bundle = new Bundle();
            bundle.putLong(Costanti.ID_RECORD_TABELLA, id_record);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    @Override
    /*Se l'utente decide tramite dialog di cancellare definiticamente una riga di database*/
    public void onFinishClickListener(String inputText) {
        if(inputText.contentEquals("delete")) {
            dbManager.delete(id_record);
            cursorAdapter.changeCursor(dbManager.query());
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
            Log.d(Costanti.NOME_APP ,
                    ": CURSORE -->  "+ dbManager.showCursor(cursorRisultato));
            progressBar.setVisibility(View.GONE);
            /* Controlla se la query ha prodotto nessun risultato */
            if(cursorRisultato.getCount()==0){
                Intent intent=new Intent();
                setResult(RESULT_FIRST_USER,intent);  /*Informa l'attività chiamante con un codice*/
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


    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
    }


}
