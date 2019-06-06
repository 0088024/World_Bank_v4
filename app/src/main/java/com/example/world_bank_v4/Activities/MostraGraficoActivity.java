package com.example.world_bank_v4.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;

import java.io.File;


public class MostraGraficoActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private String idInd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getResources().getString(R.string.NOME_APP),
                this.getClass().getCanonicalName() + ": CREATE");
        setContentView(R.layout.mostra_png_salvato_prima);
         /*Imposta se "Home" deve essere visualizzato come un'affordance "up". Impostalo su true se
        la selezione di "home" restituisce un singolo livello nell'interfaccia utente anziché
        tornare al livello principale o alla prima pagina.*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.graph);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }



    @Override
    public void onResume(){
        super.onResume();
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        /*carico dalle sharedPrefernces il nome dell'ultimo file salvato*/
        Resources res = getResources();
        String chiave_nome_file_png = res.getString(R.string.PREFERENCES_FILE_INDICATORE_PER_PAESE);
        /*leggo dalle sharedPreferences il nome del file precedentemente salvato*/
        SharedPreferences sharedPreferences =
                getSharedPreferences(chiave_nome_file_png, Context.MODE_PRIVATE);
        String idPaeseSelezionato =
                sharedPreferences.getString(res.getString(R.string.ID_PAESE_SELEZIONATO),
                        "File non esiste");
        String idIndicatoreSelezionato =
                sharedPreferences.getString(res.getString(R.string.ID_INDICATORE_SELEZIONATO),
                        "File non esiste");
        String nome_file_png = idPaeseSelezionato + "_" + idIndicatoreSelezionato;

        new CaricaFileTask(imageView).execute(nome_file_png);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return false;
    }



    /*thread che in background carica 1 file png*/
    private class CaricaFileTask extends AsyncTask<String, Integer, Bitmap> {
        private ImageView imageView;
        private int count=1;

        public CaricaFileTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Resources res = getResources();
            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (; count <= res.getInteger(R.integer.progressBarTime); count++)
                publishProgress(count);

            /*riceve la dirctory e il nome del file*/
            File file_png = new File(getApplicationContext().getFilesDir(), params[0]);
            /*Decode a file path into a bitmap. If the specified file name is null,
            or cannot be decoded into a bitmap, the function returns null.*/
            Bitmap bitmap = BitmapFactory.decodeFile(file_png.getAbsolutePath());
            Log.d(res.getString(R.string.NOME_APP), file_png.getAbsolutePath());

            if(bitmap == null)
                Log.d(res.getString(R.string.NOME_APP), "Error decoding stream Bitmap");

            return bitmap;


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        protected void onPostExecute(Bitmap risultato){
            progressBar.setVisibility(View.GONE);
            /* Controlla se non è presente nessun file png in memoria  */
            if(risultato==null){
                Intent intent=new Intent();
                setResult(RESULT_FIRST_USER,intent); // Informa l'attività chiamante con un codice
                finish(); // Non si può proseguire
            }
            else
            imageView.setImageBitmap(risultato);

        }
    }

}
