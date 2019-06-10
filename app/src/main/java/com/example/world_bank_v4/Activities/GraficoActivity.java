package com.example.world_bank_v4.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.world_bank_v4.Controller.DbManager;
import com.example.world_bank_v4.Controller.MyGSON;
import com.example.world_bank_v4.Controller.MyIValueFormatter;
import com.example.world_bank_v4.Dialog.DialogCheckNow;
import com.example.world_bank_v4.Dialog.DialogWarningData;
import com.example.world_bank_v4.Model.ElementoGenerico;
import com.example.world_bank_v4.Model.Intestazione;
import com.example.world_bank_v4.Model.RecordTabella;
import com.example.world_bank_v4.Model.ValoreGrafico;
import com.example.world_bank_v4.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GraficoActivity extends ListaGenericaActivity
        implements View.OnClickListener,DialogWarningData.OnClickListener{

    private DbManager dbManager;
    private ArrayList<ValoreGrafico> lista_grafico;      /*lista che conterrà gli oggetti Grafico*/
    private LineChart chart;
    private TextView textView_chart_titolo;
    private TextView textView_chart_sottotitolo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*"specializza activity*/
        setContentView(R.layout.activity_grafico);
    }


    @Override
    public void onResume(){
        /*in this example, a LineChart is initialized from xml*/
        Resources res = getResources();
        getSupportActionBar().setLogo(R.drawable.graph);
        chart = findViewById(R.id.chart);
        textView_chart_titolo = findViewById(R.id.textView_chart_titolo);
        textView_chart_sottotitolo = findViewById(R.id.textView_chart_sottotitolo);

        super.setProgressBar(R.id.progressBar);
        lista_grafico = new ArrayList<ValoreGrafico>();
        TypeToken<ArrayList<ValoreGrafico>> listTypeToken =
                new TypeToken<ArrayList<ValoreGrafico>>() {};
        super.setTypeToken(listTypeToken);
        super.setKEY_JSON_FILE(res.getString(R.string.KEY_JSON_FILE_INDICATORE_PER_PAESE));
        super.setNOME_FILE_PREFERENCES(res.getString(R.string.PREFERENCES_FILE_INDICATORE_PER_PAESE));
        /*per costruire l'api devo aspettare che la classe ListaIndicatoriActivity mi passi
        l'intento con il Paese selezionato dall'utente*/
        super.setAPI_WORLD_BANK(null);
        super.onResume();
    }




    /*se le risorse sono aperte, le chiude*/
    @Override
    protected void onDestroy(){
        if(dbManager != null) {  /*potrebbe essere null se non è stato mai aperto in GraficoActivity e
                                l'utente torna indietro.*/
            dbManager.close();
        }
        super.onDestroy();
    }



    @Override
    public String costruisciApi(){
        /*costruisci la stringa api per ottenere una lista di valori relativi
        all'indicatore per paese selezionato*/
        StringBuilder api_indicatore_per_paese = new StringBuilder();
        /*API_COUNTRY_LIST = "https://api.worldbank.org/v2/country/"*/
        api_indicatore_per_paese.append(getResources().getString(R.string.API_COUNTRY_LIST));
        api_indicatore_per_paese.append(super.getIdPaeseSelezionato());
        api_indicatore_per_paese.append("/indicator/");
        api_indicatore_per_paese.append(super.getIdIndicatoreSelezionato());
        /*API_INDICATORE_PER_PAESE
        https://api.worldbank.org/v2/country/idPaese/indicator?format=json&per_page=10000*/
        api_indicatore_per_paese.append("?format=json&&per_page=10000");
        Log.d(getResources().getString(R.string.APP_NAME),
                "api_indicatore_per_paese:  " + api_indicatore_per_paese);
        return api_indicatore_per_paese.toString();
    }



    /*se c'è connessione riceve il file json, se è corretto lo trasforma con GSON in una List<T>,
    e collega quest'ultima al chart*/
    @Override
    public void caricaLayout(){


            String json_file = (super.getJsonFile());  // Recupera il relativo file json

            /*DEBUG*/
            Log.d(getResources().getString(R.string.APP_NAME) + "JSON FILE ", json_file);

            /*con la libreria GSON ottengo la corrispondente lista di indicatori del file json*/
            MyGSON myGSON = new MyGSON(this);
            lista_grafico = myGSON.getListFromJson(json_file,
                    new TypeToken<ArrayList<ValoreGrafico>>() {});

            /*Controlla se non ci sono dati per costruire il grafico*/
            if(lista_grafico == null){
                DialogWarningData mydialog = new DialogWarningData();

                Bundle bundle = new Bundle();
                Resources res = getResources();
                /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
                bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG),
                        res.getStringArray(R.array.stringhe_dialog_no_graph));
                bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.missing);
                /*servirà per utilizzare la riflessione nella dialog*/
                bundle.putBoolean(res.getString(R.string.KEY_IS_NO_GRAPH_DIALOG), true);
                mydialog.setArguments(bundle);

                mydialog.show(getSupportFragmentManager(), res.getString(R.string.MY_DIALOG));
            }

            /*costruisci grafico in un thread in background*/
            else new CostruisciGraficoTask().execute();


    }



    /*a seconda del bottone che è stato cliccato, lancia il relativo thread task in bakground*/
    @Override
    public void onClick(View v) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_di_scelta_grafico_activity, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent intent;
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_salva_grafico:
                new SalvaGraficoTask().execute(chart);
                return false;

            case R.id.menu_salva_database:
                new SalvaDatabaseTask().execute(lista_grafico);
                return false;
            case R.id.ritorno_home:
                setResult(getResources().getInteger(R.integer.BACK_HOME));

        }
        finish();
        return false;
    }

    @Override
    public void onFinishClickListener(String inputText) {
        Resources res = getResources();
        if(inputText.contentEquals(res.getString(R.string.FINISH))) {
            finish();

        }
    }


    private class CostruisciGraficoTask extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            costruisciGrafico();
            return "Grafico costruito";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            getProgressBar().setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected void onPostExecute(String risultato) {
            Log.d(getResources().getString(R.string.APP_NAME), risultato);
            getProgressBar().setVisibility(View.GONE);
            textView_chart_titolo.setText(getNomePaeseSelezionato());
            textView_chart_sottotitolo.setText(getNomeIndicatoreSelezionato());
            chart.invalidate(); /*refresh. La chiamata di questo metodo sul grafico si aggiornerà
            (ridisegna). Questo è necessario per rendere effettive le modifiche apportate al
            grafico*/

        }

    }




    /*thread che in background salva i dati nel database locale*/
    private class SalvaDatabaseTask extends AsyncTask< ArrayList<ValoreGrafico>, Integer, String > {

        private DbManager dbManager;
        private int count = 1;



        public SalvaDatabaseTask() {

            this.dbManager = new DbManager(getApplicationContext()); /*oggetto per interagire con il
                                                                                database*/
            setDbManager(dbManager);        /*passa alla classe contenitore un riferimento al
                                            dbManager così lo potrà chiudere nella onDestroy()*/
        }


        @Override
        protected String doInBackground(ArrayList<ValoreGrafico>... params) {

            ArrayList<ValoreGrafico> lista_Valore_grafico = params[0];
            /*costruisci un oggetto recordTabella corrispondente all'indicatore per paese ottenuto*/
            /*con la libreria GSON ottengo il corrispondente primo oggetto dell'array di elementi
            del file json*/
            MyGSON myGSON = new MyGSON(getApplicationContext());
            String json_file = (getJsonFile());  // Recupera il relativo file json

            JsonElement jsonElement = myGSON.getJsonElementList(json_file, 0);
            ElementoGenerico country = myGSON.getObjectIntoElement(jsonElement,
                    "country");
            ElementoGenerico indicator = myGSON.getObjectIntoElement(jsonElement,
                    "indicator");

            Intestazione intestazione = myGSON.getJsonElementIntestazione(json_file);
            /*Get the local Time*/
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format =
                    new SimpleDateFormat("dd-MM-YYYY  HH:mm:ss", Locale.ENGLISH);
            String myTime = format.format(calendar.getTime());

            RecordTabella recordTabella = new RecordTabella(intestazione, myTime, country, indicator,
                    lista_Valore_grafico);

            dbManager.addRow(recordTabella);

            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (; count <= getResources().getInteger(R.integer.progressBarTime); count++)
                publishProgress(count);


            return "Data saved in database";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            getProgressBar().setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected void onPostExecute(String risultato) {
            Log.d(getResources().getString(R.string.APP_NAME), risultato);
            getProgressBar().setVisibility(View.GONE);

            DialogCheckNow mydialog = new DialogCheckNow();
            Bundle bundle = new Bundle();
            Resources res = getResources();
            /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
            bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG),
                    res.getStringArray(R.array.stringhe_dialog_check_now));
            bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.successfull);
            /*servirà per utilizzare la riflessione nella dialog*/
            bundle.putString(res.getString(R.string.KEY_ITEM_MENU_SELEZIONATO),
                    res.getString(R.string.button_salva_database));
            mydialog.setArguments(bundle);
            mydialog.show(getSupportFragmentManager(),
                    getResources().getString(R.string.MY_DIALOG));

        }

    }



    /*thread che in background salva il grafico in un file png*/
    private class SalvaGraficoTask extends AsyncTask< Chart, Integer, String > {

        private int count =1;


        @Override
        protected String doInBackground(Chart ... params) {

            Chart chart = params[0];
            /* Returns the Bitmap object that represents the chart, this Bitmap always contains the
            latest drawing state of the chart.*/
            LinearLayout view = findViewById(R.id.linearLayoutGrafico);
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                                            Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            /*disegna la vista nel canvas che a sua volta avvole il bitmpa*/
            view.draw(canvas);

            /*stream per scrivere il grafico bitmap sul disco*/
            FileOutputStream outputStream;
            String nome_file_png = getIdPaeseSelezionato() + "_" +
                    getIdIndicatoreSelezionato();
            try{
                /*apre 1 stream in scrittura verso 1 file nello storage interno, privato per l'app.
                se il file non esiste lo crea*/
                outputStream =
                        openFileOutput(nome_file_png, Context.MODE_PRIVATE);
                /*la qualità 80% vale solo se il formato è JPEG.
                Write a compressed version of the bitmap to the specified outputstream.
                If this returns true, the bitmap can be reconstructed by passing a
                corresponding inputstream to BitmapFactory.decodeStream(). Note: not all Formats
                support all bitmap configs directly, so it is possible that the returned bitmap
                from BitmapFactory could be in a different bitdepth, and/or may have lost per-pixel
                alpha (e.g. JPEG only supports opaque pixels).*/
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 80 , outputStream)){
                    Log.d(getResources().getString(R.string.APP_NAME),
                            "chart_bitmap compressed in PNG file");
                }
                else  Log.d(getResources().getString(R.string.APP_NAME),
                        "Error compressed bitmap in PNG file");

                outputStream.close();

            } catch (FileNotFoundException e) {
                Log.d(getResources().getString(R.string.APP_NAME), e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(getResources().getString(R.string.APP_NAME), e.getMessage());
                e.printStackTrace();
            }

            // Fammi vedere per un certo tempo stabilito da una costante la Progress Bar
            for (; count <= getResources().getInteger(R.integer.progressBarTime); count++)
                publishProgress(count);


            return nome_file_png + " Saved" ;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            getProgressBar().setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(String risultato){
            Log.d(getResources().getString(R.string.APP_NAME), risultato);
            getProgressBar().setVisibility(View.GONE);

            DialogCheckNow mydialog = new DialogCheckNow();
            Bundle bundle = new Bundle();
            Resources res = getResources();
            /*inserisce nel bundle le stringhe e l'icona che la dialog deve mostrare*/
            bundle.putStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG),
                    res.getStringArray(R.array.stringhe_dialog_check_now));
            bundle.putInt(res.getString(R.string.KEY_ID_ICONA), R.drawable.successfull);
            /*servirà per utilizzare la riflessione nella dialog*/
            bundle.putString(res.getString(R.string.KEY_ITEM_MENU_SELEZIONATO),
                    res.getString(R.string.button_salva_grafico));
            mydialog.setArguments(bundle);
            mydialog.show(getSupportFragmentManager(),
                    getResources().getString(R.string.MY_DIALOG));

        }
    }



    /*imposta tutte le caratteristiche desiderate del grafico*/
    public void costruisciGrafico() {


       /* int blu_grafico = getResources().getValue(R.color.blu_grafico);*/
        int blu_grafico = ContextCompat.getColor(this, R.color.blu_grafico);
        /*imposta etichetta descrizione*/
        Description description = chart.getDescription();
        description.setText("ANNI");
        description.setTextSize(11f);
        description.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        description.setTextAlign(Paint.Align.RIGHT);

        chart.setClipValuesToContent(true);
        chart.setDrawGridBackground(true);
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.BLACK);
        chart.setBorderWidth(1.5f);
        chart.setAutoScaleMinMaxEnabled(true);      /*Flag that indicates if auto scaling on the y
        axis is enabled. If enabled the y axis automatically adjusts to the min and max y values of
        the current x axis range ogni volta che cambia la vista. Default: false*/
        chart.setTouchEnabled(true);    /*Default = true*/

        /*imposta leggenda*/
        Legend legend = chart.getLegend();
        legend.setTextColor(blu_grafico);
        legend.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        legend.setTextSize(16);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        /*legenda personalizzata*/
        legend.setXEntrySpace(20f);
        LegendEntry legendEntry = new LegendEntry();
        legendEntry.form = Legend.LegendForm.LINE;
        legendEntry.formColor = blu_grafico;
        legendEntry.label = "estimated values";
        legendEntry.formSize = 12;
        LegendEntry legendEntry2 = new LegendEntry();
        legendEntry2.form = Legend.LegendForm.CIRCLE;
        legendEntry2.formColor = blu_grafico;
        legendEntry2.label = "reported values";
        legendEntry2.formLineWidth = 2;
        legendEntry2.formSize = 10;
        LegendEntry[] legends = {legendEntry, legendEntry2};
        legend.setCustom(legends);



        /*imposta asse y destro e sinistro*/
        YAxis yAxisleft = chart.getAxisLeft();  /*Per default tutti i dati aggiunti al grafico
                                                vengono confrontati con YAxis sinistro del grafico*/
        yAxisleft.setDrawGridLines(true);       //linea della griglia
        yAxisleft.setDrawZeroLine(true);        //disegna una linea zero
        yAxisleft.setZeroLineColor(Color.GRAY);
        yAxisleft.setTextSize(13);
        yAxisleft.setSpaceBottom(10f);
        yAxisleft.setSpaceTop(15f);
        yAxisleft.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        yAxisleft.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                float temp = value - (int)value;            /*parte decimale di value*/
                String string = String.valueOf(temp);       /*la trasformo in stringa*/
                String subString = string.substring(0, 3);  /*prendiamo solo 1 valore dopo
                                                            la virgola per aumentare area grafico*/

                return String.valueOf((int)value + Float.valueOf(subString).floatValue());

            }
        });

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setTextSize(13);
        yAxisRight.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        yAxisRight.setSpaceBottom(10f);
        yAxisRight.setSpaceTop(15f);
        yAxisRight.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                float temp = value - (int)value;            /*parte decimale di value*/
                String string = String.valueOf(temp);       /*la trasformo in stringa*/
                String subString = string.substring(0, 3);  /*prendiamo solo 1 valore dopo
                                                            la virgola per aumentare area grafico*/

                return String.valueOf((int)value + Float.valueOf(subString).floatValue());
            }
        });



        /*imposta asse x*/
        XAxis xAxis = chart.getXAxis();      /*acquisisce 1 istanza dell'asse x*/
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(30f);    /*imposta angolo dele etichette asse x (in gradi)*/
        xAxis.setDrawGridLines(true);
        float[] array_float = {6f, 6f};
        DashPathEffect dashPathEffect = new DashPathEffect(array_float, 0f);
        xAxis.setGridDashedLine(dashPathEffect);
        xAxis.setGranularity(1f);            /*only intervals of 1*/
        xAxis.setTextSize(13);
        /*questo metodo serve per avere gli anni senza il punto che indica il millesimo*/
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return String.valueOf((int)value); /*lo casto in 1 int per eliminare la virgola*/
            }
        });
        xAxis.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        xAxis.setAvoidFirstLastClipping(true);



        /*wrap ogegtti ValoreGrafico dentro Entry*/
        ValoreGrafico graf;
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = lista_grafico.size(); i > 0; i--) {
            graf = lista_grafico.get(i - 1);
            if (graf.getvalue() == null) {
                graf.resetValue();
            }
            /*A Entry represents one single entry in the chart. Entry(float x, float y)*/
            entries.add(new Entry(Float.parseFloat(graf.getDate()), graf.getvalue()));
        }

        /*imposta LineDataSet:rappresenta le caretteristiche comuni(style) di 1 insieme di valori*/
        /*add entries to dataset: LineaDataSet( Entry yVals, String label);*/
        final LineDataSet dataSet = new LineDataSet(entries, "estimated");
        dataSet.setColor(blu_grafico);
        /*dataSet.enableDashedLine(20f,20f, 0f);*/  /*linea tratteggiata*/
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(2.5f);
        dataSet.setCircleColor(blu_grafico);
        dataSet.setCircleHoleColor(blu_grafico);
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(blu_grafico);
        dataSet.setFillAlpha(85);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return 0f;
            }
        });
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.RED);
        dataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dataSet.setDrawValues(true);

        /*formattiamo i valori disegnati all'interno del grafico*/
        dataSet.setValueFormatter(new MyIValueFormatter(dataSet));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);



    }



    @Override
    protected void setProgressBarVisible(){
        getProgressBar().setVisibility(ProgressBar.VISIBLE);
    }


    @Override
    protected void setProgressBarGone(){
        super.getProgressBar().setVisibility(View.GONE);
    }


    public void setDbManager(DbManager dbManager){
        this.dbManager = dbManager;
    }


    public ProgressBar getProgressBar() { return super.getProgressBar(); }

}
