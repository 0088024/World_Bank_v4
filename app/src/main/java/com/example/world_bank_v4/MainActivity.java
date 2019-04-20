package com.example.world_bank_v4;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private URL url;
    private Intent intent;

    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setLogo(R.drawable.world_bank);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iv = findViewById(R.id.imageView);
        iv.setImageResource(R.drawable.wb);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_di_scelta,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent intent;
        int id = item.getItemId();
        switch(id) {
            case R.id.Menu_1:
                try {
                    url = new URL(Costanti.API_COUNTRY_LIST_PER_PAGE_500);
                }
                /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
                catch (MalformedURLException e) {
                    Log.d(Costanti.NOME_APP, e.getMessage());
                }
                new DownloadFileTask().execute(url);
                break;

            case R.id.Menu_2:
                try {
                    url = new URL(Costanti.API_TOPIC_LIST);
                }
                /*if no protocol is specified, or an unknown protocol is found, or spec is null*/
                catch (MalformedURLException e) {
                    Log.d(Costanti.NOME_APP, e.getMessage());
                }
                new DownloadFileTask().execute(url);
                break;

            case R.id.Menu_3:
                intent = new Intent(this, MostraPngSalvatoPrecedentemente.class);
                startActivity(intent);
                break;

            case R.id.Menu_4:
                intent = new Intent(this, CaricaDati.class);
                startActivity(intent);
        }
        return false;
    }


    /*thread che in background scarica in una stringa il file json dei paesi*/
    private class DownloadFileTask extends AsyncTask<URL, Void, String> {

        private InputStream risposta;
        private StringBuilder sb;
        private HttpURLConnection client;

        @Override
        protected String doInBackground(URL... urls) {

            try {
                /*creo l'oggetto HttpURLConnection e apro la connessione al server*/
                client = (HttpURLConnection) urls[0].openConnection();

                /*Recupero le informazioni inviate dal server*/
                risposta = new BufferedInputStream(client.getInputStream());

                /*leggo i caratteri e li appendo in sb*/
                sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(risposta));
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }

            }  catch (IOException e) {
                Log.d(Costanti.NOME_APP, e.getMessage());

            } finally {
                client.disconnect();
            }

            /*convert StringBuilder to String using toString() method*/
            return sb.toString();
        }

        protected void onPostExecute(String risultato){
            int requestCode = 1;
            intent = new Intent(getApplicationContext(), ListaPaesiActivity.class);
            intent.putExtra(Costanti.KEY_JSON_FILE_COUNTRY, risultato);
            startActivityForResult(intent, requestCode);
        }
    }


    @Override
    protected void onActivityResult(int requestCodeID, int resultCode, Intent intent){

    }


}

