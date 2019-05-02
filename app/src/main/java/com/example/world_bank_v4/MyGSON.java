package com.example.world_bank_v4;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MyGSON {


    public MyGSON(){

    }


    public ArrayList getList(String file_json, TypeToken typeToken){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di elementi
        dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);

        /*DEBUG*/
        JsonArray array_json = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(Costanti.NOME_APP + " DIM[]", String.valueOf(array_json.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti<T>, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        ArrayList list = gson.fromJson(je2, typeToken.getType());

        return list;
    }



    public JsonElement getJsonElementList(String file_json, int index){
        /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di
        elementi dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);              /*ottengo il jsonElement che corrisponde all'
                                                    array json di oggetti*/

        JsonArray elementi_grafico = je2.getAsJsonArray();  /*ottengo l'array json di oggetti*/
        JsonElement je_primo_elemento_grafico =
                elementi_grafico.get(index);              /*ottengo il jsonElement che corrisponde
                                                          al 1° oggetto dell'array json di oggetti*/

        return je_primo_elemento_grafico;

    }


    /*restituisce un MyElementoGenerico corrispondente all'elemento json memberName contenuto
    nell'elemento jsonElement*/
    public MyElementoGenerico getObjectIntoElement(JsonElement jsonElement, String memberName){

        /*ottengo l'ennesimo elemento all'interno di un elemento json*/
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement element = jsonObject.get(memberName);

        Gson gson = new Gson();
        MyElementoGenerico elementoGenerico = gson.fromJson(element, MyElementoGenerico.class);

        return elementoGenerico;

    }


}
