package com.example.world_bank_v4.Activities;

import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Costanti.NOME_APP, this.getClass().getCanonicalName() + ": CREATE");
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
        new CaricaFileTask(imageView).execute(Costanti.NOME_UNICO_FILE_PNG);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Intent intent=new Intent();
        //setResult(RESULT_OK,intent); // Informa l'attività chiamante con un codice
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
            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (; count<= Costanti.progressBarTime;count++)
                publishProgress(count);

            /*Decode a file path into a bitmap. If the specified file name is null,
            or cannot be decoded into a bitmap, the function returns null.*/
            File file_png = new File(getApplicationContext().getFilesDir(), params[0]);
            Bitmap bitmap = BitmapFactory.decodeFile(file_png.getAbsolutePath());
            Log.d(Costanti.NOME_APP,file_png.getAbsolutePath());
            /*il problema di questa classe è che non lancia eccezioni per gli errori ma scrive in
            Log.E*/ /*utilizzare ImageDecoder inserita nelle ultime api????--> problema con
            compatibilità retroattiva*/
            if(bitmap == null)
                Log.d(Costanti.NOME_APP, "Errore decodifica stream Bitmap");

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
