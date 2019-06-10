package com.example.world_bank_v4.Activities;


import android.content.Intent;
import android.content.res.Resources;
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
import com.example.world_bank_v4.Dialog.DialogWarningData;
import com.example.world_bank_v4.R;
import com.example.world_bank_v4.Dialog.DialogDeleteRow;



/*activity che carica e visualizza le n-tuple salvate nel database*/
public class CaricaDatiActivity extends AppCompatActivity implements View.OnClickListener,
        DialogDeleteRow.OnClickListener, DialogWarningData.OnClickListener {


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
        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": CREATE");
    }



    @Override
    public void onResume() {
        super.onResume();
        /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.round_playlist_add_black_36dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": RESUME");
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.list_view_carica_dati);
        new CaricaDatabaseTask().execute();
    }



    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": RESTART");
    }





    @Override
    protected void onPause(){
        super.onPause();
        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": PAUSE");
    }



    /*se le risorse sono aperte, le chiude*/
    /*chiude il database: è ottimale lasciare aperta la connessione al database x tutto il tempo
    necessario ad accedervi, in quanto getWritableDatabase() getReadableDatabase() sono
    costosi da chiamare. Tuttavia forse è meglio rilasciarlo qui le risorse perchè in caso di
    poca memoria la onDestroy() potrebbe non essere chiamata*/
    @Override
    protected void onDestroy(){
        Log.d(getResources().getString(R.string.APP_NAME),
                this.getClass().getCanonicalName() + ": DESTROY");
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
            mydialog.show(getSupportFragmentManager(),
                    getResources().getString(R.string.MY_DIALOG));

        }

        if(v.getId() == R.id.imageButtonDati){
            intent = new Intent(this, VisualizzaDatiActivity.class);
            bundle = new Bundle();
            bundle.putLong(getResources().getString(R.string.ID_RECORD_TABELLA), id_record);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    @Override
    /*Se l'utente decide tramite dialog di cancellare definiticamente una riga di database
    * la riga viene cancellata e la tabella si aggiorna */
    public void onDeleteClickListener(String inputText) {
        Resources res = getResources();
        if(inputText.contentEquals(res.getString(R.string.DELETE))){
            dbManager.delete(id_record);
            cursorAdapter.changeCursor(dbManager.query());
        }
    }

    @Override
     /* Una volta che l'utente ha preso visione tramite la dialaog che non c'è
    nessun database salvato l'attività termina e torna in primo piano l'altra
     */
    public void onFinishClickListener(String inputText) {
        Resources res = getResources();
        if(inputText.contentEquals(res.getString(R.string.FINISH))){
            finish();

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
            for (; count <= getResources().getInteger(R.integer.progressBarTime); count++)
                publishProgress(count);

            return cursor;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected void onPostExecute(Cursor cursorRisultato){
            Log.d(getResources().getString(R.string.APP_NAME),
                    ": CURSORE -->  "+ dbManager.showCursor(cursorRisultato));
            progressBar.setVisibility(View.GONE);
            /* Controlla se la query ha prodotto nessun risultato */
            if(cursorRisultato.getCount()==0){
                DialogWarningData mydialog = new DialogWarningData();
                Bundle bundle = new Bundle();
                Resources res = getResources();
                /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
                bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG_CHECK_WARNING_DATA),
                        res.getStringArray(R.array.stringhe_dialog_data_missing));
                bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.warning);
                /*servirà per utilizzare la riflessione nella dialog*/

                mydialog.setArguments(bundle);
                mydialog.show(getSupportFragmentManager(),
                        getResources().getString(R.string.MY_DIALOG));
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
