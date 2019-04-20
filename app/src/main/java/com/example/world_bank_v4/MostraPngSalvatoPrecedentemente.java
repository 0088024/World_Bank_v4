package com.example.world_bank_v4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MostraPngSalvatoPrecedentemente extends AppCompatActivity {

    private String nome_file;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_png_salvato_precedentemente);
        imageView = findViewById(R.id.imageView);
        new CaricaFileTask(imageView).execute(Costanti.NOME_UNICO_FILE_PNG);
    }


    /*thread che in background carica 1 file png*/
    private class CaricaFileTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public CaricaFileTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            /*Decode a file path into a bitmap. If the specified file name is null,
            or cannot be decoded into a bitmap, the function returns null.*/
            File file_png = new File(getApplicationContext().getFilesDir(), params[0]);
            Bitmap bitmap = BitmapFactory.decodeFile(file_png.getAbsolutePath());
            /*il problema di questa classe è che non lancia eccezioni per gli errori ma scrive in
            Log.E*/ /*utilizzare ImageDecoder inserita nelle ultime api????--> problema con
            compatibilità retroattiva*/
            if(bitmap == null)
                Log.d(Costanti.NOME_APP, "Errore decodifica stream Bitmap");
            return bitmap;


        }

        protected void onPostExecute(Bitmap risultato){
            int requestCode = 1;


            imageView.setImageBitmap(risultato);
            Log.d(Costanti.NOME_APP, "cucu");


        }
    }

}
