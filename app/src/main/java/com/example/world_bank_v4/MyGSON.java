package com.example.world_bank_v4;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MyGSON {

    private final String Nome_App = "WorldBank: ";
    private final String PAESE = "Paese";
    private final String INDICATORE = "Indicatore";
    private final String ARGOMENTO = "Argmomento";
    private final String GRAFICO = "Grafico";


    public MyGSON(){

    }

    public ArrayList getListPaesi(String file_json){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di elementi
        dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_paesi = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(Nome_App + " DIM PAESI[]", String.valueOf(array_paesi.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Paese>> listType = new TypeToken<ArrayList<Paese>>() {};
        ArrayList list = gson.fromJson(je2, listType.getType());

        return list;
    }

    public ArrayList getListArgomenti(String file_json){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di elementi
        dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_argomento = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(Nome_App + " DIM PAESI[]", String.valueOf(array_argomento.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Argomento>> listType =
                                            new TypeToken<ArrayList<Argomento>>() {};
        ArrayList list  = gson.fromJson(je2, listType.getType());

        return list;
    }

    public ArrayList getListIndicatori(String file_json){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di elementi
        dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_indicatore = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(Nome_App + " DIM PAESI[]", String.valueOf(array_indicatore.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Indicatore>> listType = new TypeToken<ArrayList<Indicatore>>() {};
        ArrayList list  = gson.fromJson(je2, listType.getType());

        return list;
    }

    public ArrayList getListDatiGrafico(String file_json){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di elementi
        dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_grafico = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(Nome_App + " DIM PAESI[]", String.valueOf(array_grafico.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti Paese, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        TypeToken<ArrayList<Grafico>> listType = new TypeToken<ArrayList<Grafico>>() {};
        ArrayList list  = gson.fromJson(je2, listType.getType());

        return list;
    }
}
